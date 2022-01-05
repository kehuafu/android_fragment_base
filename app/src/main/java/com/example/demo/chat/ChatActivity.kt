package com.example.demo.chat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.example.demo.R
import com.example.demo.app.AppManager
import com.example.demo.chat.adapter.ChatFileTypeAdapter
import com.example.demo.chat.adapter.ChatListMultipleAdapter
import com.example.demo.databinding.FragmentChatBinding
import com.example.demo.chat.bean.Message
import com.example.demo.chat.bean.MessageTheme
import com.example.demo.common.receiver.LocalEventLifecycleViewModel
import com.example.demo.common.receiver.event.LocalLifecycleEvent
import com.kehuafu.base.core.container.base.BaseActivity
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV2
import com.kehuafu.base.core.container.widget.toast.showToast
import com.kehuafu.base.core.ktx.showHasResult
import com.tencent.imsdk.v2.V2TIMMessage
import java.util.*
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.chat.mvvm.MessageViewModel
import com.example.demo.databinding.LayChatInputViewBinding
import com.example.demo.utils.*
import com.example.demo.preview.PreviewActivity
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV4
import com.kehuafu.base.core.ktx.dp2px
import com.kehuafu.base.core.ktx.toJsonTxt
import com.kehuafu.base.core.ktx.viewBindings
import java.io.File
import java.lang.Exception


open class ChatActivity :
    BaseActivity<FragmentChatBinding, MessageViewModel, MessageViewModel.MessageState>(),
    BaseRecyclerViewAdapterV4.OnItemClickListener<Message>,
    LocalEventLifecycleViewModel.OnLocalEventCallback<LocalLifecycleEvent> {


    private var heightProvider: HeightProvider? = null

    private var mChatListAdapter = ChatListMultipleAdapter()

    private var mChatFileTypeAdapter = ChatFileTypeAdapter()

    private var userId: String? = ""

    private var messageList: MutableList<Message> = mutableListOf()

    private var showKeyBoardMode = KEY_BOARD_MODE_SOUND

    private lateinit var lp: WindowManager.LayoutParams

    private lateinit var mPop: PopupWindow

    private lateinit var mAudioRecodeUtils: AudioRecodeUtils

    private lateinit var rl: RelativeLayout

    companion object {

        const val EXTRAS_TARGET_ID = "com.example.demo.chat.EXTRAS_TARGET_ID"
        private const val TAG = "ChatActivity"
        private const val KEY_BOARD_MODE_SOUND = 0x01
        private const val KEY_BOARD_MODE_TEXT = 0x02
        private const val KEY_BOARD_MODE_EXPRESSION = 0x03
        private const val KEY_BOARD_MODE_FILE = 0x04

        @JvmStatic
        fun showHasResult(targetId: String) {
            ActivityUtils.getTopActivity()
                ?.showHasResult(ChatActivity::class.java) {
                    putString(EXTRAS_TARGET_ID, targetId)
                }
        }
    }

    override fun onInflateArgs(arguments: Bundle) {
        super.onInflateArgs(arguments)
        userId = arguments.getString(EXTRAS_TARGET_ID, "")
    }

    override fun onBackPressed() {
        finish()
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onViewCreated(savedInstanceState: Bundle?) {
        BarUtils.setStatusBarLightMode(this, true)
        AppManager.localEventLifecycleViewModel.register(this, this)
        heightProvider = HeightProvider(this).init()
        viewBinding.nav.backIv.setOnClickListener {
            finish()
        }

        heightProvider!!.setHeightListener {
            if (it.toFloat() > 0f) {
                viewBinding.chatRv.stopScroll()
//                AnimatorUtils.build()
//                    .startTranslateY(viewBinding.chatInputLl, (-it + dp2px(300f)))
                viewBinding.chatInputLl.translationY = (-it + dp2px(300f))
                viewBinding.frameLayout.translationY = -it.toFloat()
                viewBinding.chatRv.scrollToPosition(0)
                viewBinding.chatInputRl.etMsg.requestFocus()
                return@setHeightListener
            }
            if (showKeyBoardMode == KEY_BOARD_MODE_FILE) {
                return@setHeightListener
            } else if (showKeyBoardMode == KEY_BOARD_MODE_TEXT) {
                viewBinding.chatInputLl.translationY = (-it + dp2px(300f))
                viewBinding.frameLayout.translationY = -it.toFloat()
                return@setHeightListener
            }
            viewBinding.chatInputLl.translationY = -it + dp2px(300f)
            viewBinding.frameLayout.translationY = it.toFloat()
        }

        withViewBinding {
            nav.titleTv.text = "$userId"
            chatInputRl.ivNavMore.setOnClickListener {
                if (showKeyBoardMode == KEY_BOARD_MODE_FILE) {
                    showKeyBoardMode = KEY_BOARD_MODE_TEXT
                    KeyboardUtils.showSoftInput(this@ChatActivity)
                    viewBinding.chatInputRl.etMsg.requestFocus()
                } else {
                    showKeyBoardMode = KEY_BOARD_MODE_FILE
                    viewBinding.chatInputRl.etMsg.clearFocus()
                    chatInputRl.etMsg.isVisible = true
                    chatInputRl.tvVoice.isVisible = false
                    chatInputRl.ivVoice.setPadding(0, 0, 0, 0)
                    chatInputRl.ivVoice.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@ChatActivity,
                            R.drawable.voice_icon
                        )
                    )
                    if (heightProvider!!.isSoftInputVisible) {
                        KeyboardUtils.hideSoftInput(this@ChatActivity)
                        return@setOnClickListener
                    }
                    viewBinding.chatRv.stopScroll()
                    AnimatorUtils.build()
                        .startTranslateY(viewBinding.chatInputLl, dp2px(0f))
                    viewBinding.frameLayout.translationY = -dp2px(300f)
                    viewBinding.chatRv.scrollToPosition(0)
                }
            }
            chatInputRl.tvVoice.setOnTouchListener(View.OnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lp.alpha = 0.4f
                        window.attributes = lp
                        mPop.width = LinearLayout.LayoutParams.MATCH_PARENT
                        mPop.height = LinearLayout.LayoutParams.MATCH_PARENT
                        mPop.showAtLocation(rl, Gravity.CENTER, 0, 0)
                        mAudioRecodeUtils.startRecord()
                    }
                    MotionEvent.ACTION_UP -> {
                        //恢复背景色
                        lp.alpha = 1f
                        window.attributes = lp
                        mAudioRecodeUtils.stopRecord() //结束录音（保存录音文件）
                        mPop.dismiss()
                    }
                }
                true
            })
            chatInputRl.ivVoice.setOnClickListener {
                if (chatInputRl.etMsg.isVisible) {
                    PermissionUtils.permission(
                        PermissionConstants.MICROPHONE,
                        PermissionConstants.STORAGE
                    )
                        .callback(object : PermissionUtils.FullCallback {
                            override fun onGranted(permissionsGranted: List<String>) {
                                LogUtils.d(permissionsGranted)
                                showToast("同意授权")
                            }

                            override fun onDenied(
                                permissionsDeniedForever: List<String>,
                                permissionsDenied: List<String>
                            ) {
                                LogUtils.d(permissionsDeniedForever, permissionsDenied)
                                showToast("拒绝授权")
                                return
                            }
                        })
                        .theme { activity -> ScreenUtils.setFullScreen(activity) }
                        .request()
                    showToast("切换语音模式")
                    showKeyBoardMode = KEY_BOARD_MODE_SOUND
                    if (heightProvider!!.isSoftInputVisible) {
                        KeyboardUtils.hideSoftInput(this@ChatActivity)
                    }
                    chatInputRl.ivVoice.setPadding(6, 6, 6, 6)
                    chatInputRl.ivVoice.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@ChatActivity,
                            R.drawable.keyboard
                        )
                    )
                } else {
                    showToast("切换文本模式")
                    showKeyBoardMode = KEY_BOARD_MODE_TEXT
                    if (!heightProvider!!.isSoftInputVisible) {
                        KeyboardUtils.showSoftInput(this@ChatActivity)
                    }
                    chatInputRl.ivVoice.setPadding(0, 0, 0, 0)
                    chatInputRl.ivVoice.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@ChatActivity,
                            R.drawable.voice_icon
                        )
                    )
                }
                chatInputRl.etMsg.isVisible = !chatInputRl.etMsg.isVisible
                chatInputRl.tvVoice.isVisible = !chatInputRl.etMsg.isVisible
            }
            chatInputRl.ivExpression.setOnClickListener {
                showToast("显示我的表情包")
                showKeyBoardMode = KEY_BOARD_MODE_EXPRESSION
                chatInputRl.ivExpression.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@ChatActivity,
                        R.drawable.keyboard
                    )
                )
            }
            chatInputRl.btnSendMsg.setOnClickListener {
                withViewBinding {
                    viewModel.sendTextMsg(
                        chatInputRl.etMsg.text.toString().trim(),
                        userId!!,
                        messageList
                    )
                    viewBinding.chatRv.scrollToPosition(0)
                    chatInputRl.etMsg.text.clear()
                }
            }
            chatInputRl.etMsg.doAfterTextChanged {
                if (viewBinding.chatInputRl.etMsg.text.toString().trim().isBlank()) {
                    viewBinding.chatInputRl.btnSendMsg.visibility = View.GONE
                    viewBinding.chatInputRl.ivNavMore.visibility = View.VISIBLE
                } else {
                    viewBinding.chatInputRl.btnSendMsg.visibility = View.VISIBLE
                    viewBinding.chatInputRl.ivNavMore.visibility = View.GONE
                }
            }
            chatInputRl.etMsg.setOnClickListener {
                showKeyBoardMode = KEY_BOARD_MODE_TEXT
            }
            mChatListAdapter.setOnItemClickListener(this@ChatActivity)
            chatRv.itemAnimator = null
            chatRv.layoutManager = LinearLayoutManager(this@ChatActivity)
            (chatRv.layoutManager as LinearLayoutManager).reverseLayout = true
            chatRv.adapter = mChatListAdapter
//            chatRv.setItemViewCacheSize(10)

            mChatFileTypeAdapter.setOnItemClickListener(object :
                BaseRecyclerViewAdapterV2.OnItemClickListener<MessageTheme> {
                override fun onItemClick(itemView: View, item: MessageTheme, position: Int?) {
                    when (itemView.id) {
                        R.id.iv_type_image -> {
                            showToast("文件类型-->" + item.title)
                            PermissionUtils.permission(
                                PermissionConstants.STORAGE,
                                PermissionConstants.CAMERA,
                                PermissionConstants.LOCATION
                            )
                                .callback(object : PermissionUtils.FullCallback {
                                    override fun onGranted(permissionsGranted: List<String>) {
                                        LogUtils.d(permissionsGranted)
                                        when (item.title) {
                                            "相册" -> {
                                                launchAlbum()
                                            }
                                            "拍摄" -> {
                                                launchCameraUri()
                                            }
                                            "文件" -> {
                                                launchVideoPick()
                                            }
                                        }
                                    }

                                    override fun onDenied(
                                        permissionsDeniedForever: List<String>,
                                        permissionsDenied: List<String>
                                    ) {
                                        LogUtils.d(permissionsDeniedForever, permissionsDenied)
                                    }
                                })
                                .theme { activity -> ScreenUtils.setFullScreen(activity) }
                                .request()
                        }
                        else -> {

                        }
                    }
                }
            })
            chatFile.chatFileRv.itemAnimator = null
            chatFile.chatFileRv.layoutManager = GridLayoutManager(this@ChatActivity, 4)
            chatFile.chatFileRv.adapter = mChatFileTypeAdapter
            chatRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (heightProvider!!.isSoftInputVisible) {
                        KeyboardUtils.hideSoftInput(this@ChatActivity)
                    } else {
                        if (showKeyBoardMode == KEY_BOARD_MODE_FILE) {
                            showKeyBoardMode = KEY_BOARD_MODE_TEXT
                        }
                        viewBinding.chatInputLl.translationY = dp2px(300f)
                        viewBinding.frameLayout.translationY = dp2px(0f)
                    }
                }
            })
        }
        initAudio()
    }

    private var ltime: Long = 0

    //录音功能 初始化
    private fun initAudio() {
        rl = View.inflate(this, R.layout.popup_window, null) as RelativeLayout
        //设置空白的背景色
        lp = window.attributes
        mPop = PopupWindow(rl)
        val micImage = rl.findViewById<ImageView>(R.id.iv_pro)
        val recordingTime = rl.findViewById<TextView>(R.id.recording_time)
        mAudioRecodeUtils = AudioRecodeUtils()
        mAudioRecodeUtils.setOnAudioStatusUpdateListener(object :
            AudioRecodeUtils.OnAudioStatusUpdateListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onUpdate(db: Double, time: Long) {
                //根据分贝值来设置录音时话筒图标的上下波动
                ltime = time
                micImage.drawable.level = (3000 + 6000 * db / 100).toInt()
                recordingTime.text = LocalDateUtils.getDateCoverString(time)
            }

            override fun onStop(filePath: String) {
                if (ltime < 1500) { //判断，如果录音时间小于1.5秒，则删除文件提示，过短
                    val file = File(filePath)
                    if (file.exists()) { //判断文件是否存在，如果存在删除文件
                        file.delete() //删除文件
                        showToast("录音时间过短")
                    }
                } else {
                    try {
                        recordingTime.text = "00:00"
                        viewModel.sendSoundMsg(
                            filePath,
                            (ltime / 1000).toInt(),
                            userId!!,
                            messageList
                        )
                        ltime = 0
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    override fun onLoadDataSource() {
        super.onLoadDataSource()
        viewModel.getC2CHistoryMessageList(userId!!, true)
        viewModel.initMessageThemeList()
    }

    override fun onStateChanged(state: MessageViewModel.MessageState) {
        super.onStateChanged(state)
        when (state.currentAction) {
            is MessageViewModel.MessageAction.InitMessageThemeList -> {
                Log.e("TAG", "InitMessageThemeList: " + state.messageTheme.size)
                mChatFileTypeAdapter.resetItems(state.messageTheme)
            }
            is MessageViewModel.MessageAction.C2CHistoryMessageList -> {
                Log.e("TAG", "C2CHistoryMessageList: " + state.messageList.size)
                messageList = state.messageList
                mChatListAdapter.resetItems(messageList)
            }
            is MessageViewModel.MessageAction.MsgSendSuccess -> {
                messageList = state.messageList
                mChatListAdapter.resetItems(messageList)
            }
            is MessageViewModel.MessageAction.MsgSendFailed -> {
                messageList = state.messageList
                mChatListAdapter.resetItems(messageList)
            }
        }
    }

    private val mLauncherCameraUri =
        registerForActivityResult(TakeCameraUri()) {
            viewModel.sendImageMsg(
                UriUtil.getFileAbsolutePath(this, it),
                userId!!,
                messageList
            )
        }

    //调用相机
    protected fun launchCameraUri() {
        mLauncherCameraUri.launch(null)
    }

    //选取图片
    private val mLauncherAlbum = registerForActivityResult(
        GetContent()
    ) {
        viewModel.sendImageMsg(
            UriUtil.getFileAbsolutePath(this, it),
            userId!!,
            messageList
        )
    }

    //调用相册选择图片
    protected fun launchAlbum() {
        mLauncherAlbum.launch("image/*")
    }

    private val mActLauncherAlbum = registerForActivityResult(
        GetContent()
    ) {
        val bitmap = PathUtil.voidToFirstBitmap(UriUtil.getFileAbsolutePath(this, it))
        val firstUrl = PathUtil.bitmapToStringPath(this, bitmap!!)
        val duration = PathUtil.getLocalVideoDuration(UriUtil.getFileAbsolutePath(this, it))
        viewModel.sendVideoMsg(
            UriUtil.getFileAbsolutePath(this, it),
            firstUrl!!,
            duration,
            userId!!,
            messageList
        )
    }

    //选取视频文件（和选取相册类似）
    private fun launchVideoPick() {
        mActLauncherAlbum.launch("video/*")
    }

    override fun onPause() {
        super.onPause()
        if (heightProvider!!.isSoftInputVisible) {
            KeyboardUtils.hideSoftInput(this@ChatActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        AppManager.iCloudMessageManager.markC2CMessageAsRead(userId!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.localEventLifecycleViewModel.unRegister(this)
        MediaPlayerManager.getInstance().release()
    }

    override fun onItemClick(itemView: View, item: Message, position: Int?) {
        when (itemView.id) {
            R.id.left_message_avatar, R.id.right_message_avatar -> {
                showToast("头像")
            }
            R.id.left_message_ll, R.id.right_message_ll -> {
                showToast("文本")
            }
            R.id.iv_send_failed -> {
                showToast("重发")
                viewModel.resendMessage(item, userId!!, messageList, position!!)
            }
            R.id.msg_vv -> {
                when (item.messageType) {
                    Message.MSG_TYPE_IMAGE, Message.MSG_TYPE_VIDEO -> {
                        val msg = messageList.filter {
                            it.messageType == Message.MSG_TYPE_IMAGE || it.messageType == Message.MSG_TYPE_VIDEO
                        }
                        PreviewActivity.showHasResult(
                            msg.toJsonTxt(),
                            msg.indexOf(item)
                        )
                    }
                }
            }
            else -> {
                if (heightProvider!!.isSoftInputVisible) {
                    KeyboardUtils.hideSoftInput(this@ChatActivity)
                } else {
                    viewBinding.chatInputLl.translationY = dp2px(300f)
                    viewBinding.frameLayout.translationY = dp2px(0f)
                }
            }
        }
    }

    override suspend fun onEventCallback(event: LocalLifecycleEvent) {
        when (event) {
            is LocalLifecycleEvent.ReceivedChatMsgEvent -> {
                if (event.msg.userID.equals(userId)) {
                    val message = Message(
                        mid = event.msg.msgID,
                        uid = event.msg.userID,
                        name = event.msg.nickName,
                        avatar = event.msg.faceUrl,
                        messageContent = Message.messageContent(event.msg),
                        videoUrl = Message.getVideoUrl(event.msg),
                        imageUlr = Message.getImageUrl(event.msg),
                        messageType = event.msg.elemType,
                        messageTime = TimeUtils.date2String(TimeUtils.millis2Date(event.msg.timestamp * 1000)),
                        messageSender = event.msg.sender == AppManager.currentUserID,
                        showTime = false,
                        loading = event.msg.status == V2TIMMessage.V2TIM_MSG_STATUS_SENDING,
                        sendFailed = event.msg.status == com.tencent.imsdk.message.Message.V2TIM_MSG_STATUS_SEND_FAILED,
                        v2TIMMessage = event.msg
                    )
                    messageList.add(0, message)
                    runOnUiThread {
                        mChatListAdapter.resetItems(messageList)
                    }
                    AppManager.iCloudMessageManager.markC2CMessageAsRead(userId!!)
                }
            }
            else -> {

            }
        }
    }
}