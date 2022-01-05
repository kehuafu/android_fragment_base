package com.example.demo.chat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.example.demo.utils.HeightProvider
import com.example.demo.utils.UriUtil
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV2
import com.kehuafu.base.core.container.widget.toast.showToast
import com.tencent.imsdk.v2.V2TIMMessage
import java.util.*
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.chat.mvvm.MessageViewModel
import com.example.demo.utils.AnimatorUtils
import com.example.demo.utils.TakeCameraUri
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV4
import com.kehuafu.base.core.ktx.runOnMainThread
import com.kehuafu.base.core.ktx.runOnWorkThread
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.schedule


open class ChatFragment :
    BaseFragment<FragmentChatBinding, MessageViewModel, MessageViewModel.MessageState>(),
    BaseRecyclerViewAdapterV4.OnItemClickListener<Message>,
    LocalEventLifecycleViewModel.OnLocalEventCallback<LocalLifecycleEvent> {


    private var heightProvider: HeightProvider? = null

    private var mChatListAdapter = ChatListMultipleAdapter()

    private var mChatFileTypeAdapter = ChatFileTypeAdapter()

    private var userId: String? = ""

    private var keyBoardHeight = 0f

    private var messageList: MutableList<Message> = mutableListOf()

    private var showFileMode = false

    companion object {

        const val EXTRAS_TARGET_ID = "com.example.demo.chat.EXTRAS_TARGET_ID"
    }

    override fun onInflateArgs(arguments: Bundle) {
        super.onInflateArgs(arguments)
        userId = arguments.getString(EXTRAS_TARGET_ID, "")
        Log.e("TAG", "onInflateArgs: $userId")
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onViewCreated(savedInstanceState: Bundle?) {
        AppManager.localEventLifecycleViewModel.register(this, this)
        heightProvider = HeightProvider(requireActivity()).init()
        viewBinding.nav.backIv.setOnClickListener {
            showFileMode = false
            keyBoardHeight = 0F
            baseActivity.onBackPressed()
        }

        viewBinding.frameLayout.setOnClickListener {
            showFileMode = false
            keyBoardHeight = 0F
            if (heightProvider!!.isSoftInputVisible) {
                KeyboardUtils.hideSoftInput(requireActivity())
            } else {
                viewBinding.chatInputRl.root.translationY = 0F
                viewBinding.chatFile.root.visibility = View.INVISIBLE
            }
        }

        heightProvider!!.setHeightListener {
            if (showFileMode && keyBoardHeight != 0F) {
                viewBinding.chatInputRl.root.translationY = -keyBoardHeight
                viewBinding.chatFile.chatFileLl.minimumHeight = keyBoardHeight.toInt()
                viewBinding.frameLayout.translationY = viewBinding.chatInputRl.root.translationY
                return@setHeightListener
            }
            if (it.toFloat() > 0f) {
                AppManager.keyboardHeight = it.toFloat()
                keyBoardHeight = it.toFloat()
                viewBinding.chatRv.stopScroll()
                if (!showFileMode && keyBoardHeight != 0F) {
                    viewBinding.chatInputRl.root.translationY = -keyBoardHeight
                } else {
                    viewBinding.chatFile.chatFileLl.minimumHeight = it
                    AnimatorUtils.build()
                        .startTranslateY(viewBinding.chatInputRl.root, -it.toFloat())
                }
                viewBinding.chatRv.scrollToPosition(0)
                if (viewBinding.chatRv.findViewHolderForLayoutPosition(0) != null) {
                    viewBinding.frameLayout.translationY = viewBinding.chatInputRl.root.translationY
                    viewBinding.chatInputRl.etMsg.requestFocus()
                } else {
                    viewBinding.frameLayout.translationY = -it.toFloat()
                }
                return@setHeightListener
            }
            viewBinding.chatFile.root.visibility = View.INVISIBLE
            viewBinding.chatInputRl.root.translationY = -it.toFloat()
            viewBinding.frameLayout.translationY = -it.toFloat()
        }

        withViewBinding {
            nav.titleTv.text = "$userId"
            chatInputRl.root.setOnTouchListener { v, event ->
                true
            }
            chatInputRl.ivNavMore.setOnClickListener {
                showFileMode = !showFileMode
                showToast("切换文件模式$showFileMode")
                if (showFileMode) {
                    viewBinding.chatFile.root.visibility = View.VISIBLE
                    if (heightProvider!!.isSoftInputVisible) {
                        KeyboardUtils.hideSoftInput(requireActivity())
                        chatInputRl.etMsg.isVisible = true
                        chatInputRl.tvVoice.isVisible = false
                    } else if (keyBoardHeight == 0F) {
                        if (AppManager.keyboardHeight != 0F) {
                            viewBinding.chatFile.chatFileLl.minimumHeight =
                                AppManager.keyboardHeight.toInt()
                            viewBinding.chatInputRl.root.translationY = -AppManager.keyboardHeight
                            viewBinding.frameLayout.translationY =
                                viewBinding.chatInputRl.root.translationY
                        } else {
                            viewBinding.chatInputRl.root.translationY =
                                -viewBinding.chatFile.root.height.toFloat()
                            viewBinding.frameLayout.translationY =
                                viewBinding.chatInputRl.root.translationY
                        }
                    }
                } else {
                    KeyboardUtils.showSoftInput(requireActivity())
                }
            }
            chatInputRl.ivVoice.setOnClickListener {
                showFileMode = false
                keyBoardHeight = 0F
                if (chatInputRl.etMsg.isVisible) {
                    showToast("切换语音模式")
                    if (heightProvider!!.isSoftInputVisible) {
                        KeyboardUtils.hideSoftInput(requireActivity())
                    } else {
                        viewBinding.chatFile.root.visibility = View.INVISIBLE
                        viewBinding.chatInputRl.root.translationY = -keyBoardHeight
                        viewBinding.frameLayout.translationY = -keyBoardHeight
                    }
                    chatInputRl.ivVoice.setPadding(6, 6, 6, 6)
                    chatInputRl.ivVoice.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.keyboard
                        )
                    )
                } else {
                    showToast("切换文本模式")
                    if (!heightProvider!!.isSoftInputVisible) {
                        KeyboardUtils.showSoftInput(requireActivity())
                    }
                    chatInputRl.ivVoice.setPadding(0, 0, 0, 0)
                    chatInputRl.ivVoice.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.voice_icon
                        )
                    )
                }
                chatInputRl.etMsg.isVisible = !chatInputRl.etMsg.isVisible
                chatInputRl.tvVoice.isVisible = !chatInputRl.etMsg.isVisible
            }
            chatInputRl.ivExpression.setOnClickListener {
                showFileMode = false
                showToast("显示我的表情包")
                chatInputRl.ivExpression.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
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
                    if (viewBinding.chatRv.canScrollVertically(-1) || viewBinding.chatRv.canScrollVertically(
                            1
                        )
                    ) {
                        viewBinding.frameLayout.translationY = -keyBoardHeight
                    } else if (viewBinding.chatRv.findViewHolderForLayoutPosition(0) != null) {
                        viewBinding.frameLayout.translationY =
                            -viewBinding.chatInputRl.root.translationY
                    }
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
                showFileMode = false
                keyBoardHeight = 0F
            }
            mChatListAdapter.setOnItemClickListener(this@ChatFragment)
            chatRv.itemAnimator = null
            chatRv.layoutManager = LinearLayoutManager(requireActivity())
            (chatRv.layoutManager as LinearLayoutManager).reverseLayout = true
            chatRv.adapter = mChatListAdapter

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
            chatFile.chatFileRv.layoutManager = GridLayoutManager(requireActivity(), 4)
            chatFile.chatFileRv.adapter = mChatFileTypeAdapter
            chatRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (heightProvider!!.isSoftInputVisible) {
                        KeyboardUtils.hideSoftInput(requireActivity())
                    }
                }
            })
        }
    }

    override fun onLoadDataSource() {
        super.onLoadDataSource()
        viewModel.initMessageThemeList()
        viewModel.getC2CHistoryMessageList(userId!!, true)
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
                Timer().schedule(200) {
                    runOnMainThread({
                        mChatListAdapter.resetItems(messageList)
                    })
                }
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
                UriUtil.getFileAbsolutePath(requireContext(), it),
                userId!!,
                messageList
            )
        }

    //调用相册选择图片
    protected fun launchCameraUri() {
        mLauncherCameraUri.launch(null)
    }

    //选取图片
    private val mLauncherAlbum = registerForActivityResult(
        GetContent()
    ) {
        viewModel.sendImageMsg(
            UriUtil.getFileAbsolutePath(requireContext(), it),
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
        //UriUtil.getFileAbsolutePath(this, it),
        val bitmap = voidToFirstBitmap(UriUtil.getFileAbsolutePath(requireContext(), it))
        val firstUrl = bitmapToStringPath(requireContext(), bitmap!!)
        val duration = getLocalVideoDuration(UriUtil.getFileAbsolutePath(requireContext(), it))
        viewModel.sendVideoMsg(
            UriUtil.getFileAbsolutePath(requireContext(), it),
            firstUrl!!,
            duration,
            userId!!,
            messageList
        )
    }

    /**
     * 获取视频首帧图并转化为bitmap
     * @param videoUrl
     * @return
     */
    private fun voidToFirstBitmap(videoUrl: String): Bitmap? {
        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(videoUrl)
        return metadataRetriever.frameAtTime
    }

    /**
     * 将bitmap转化成本地图片路径
     * @param context
     * @param bitmap
     * @return
     */
    private fun bitmapToStringPath(context: Context, bitmap: Bitmap): String? {
        val filePic: File
        val savePath: String = PathUtils.getExternalAppCachePath()
        try {
            filePic = File(savePath + UUID.randomUUID().toString() + ".jpg")
            if (!filePic.exists()) {
                filePic.parentFile.mkdirs()
                filePic.createNewFile()
            }
            val fos = FileOutputStream(filePic)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return filePic.absolutePath
    }

    /**
     * get Local video duration
     *
     * @return
     */
    open fun getLocalVideoDuration(videoPath: String?): Int {
        //除以 1000 返回是秒
        val duration: Int
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(videoPath)
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                .toInt() / 1000

            //时长(毫秒)
            //String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            //宽
            val width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            //高
            val height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
        return duration
    }

    //选取视频文件（和选取相册类似）
    private fun launchVideoPick() {
        mActLauncherAlbum.launch("video/*")
    }


    override fun onPause() {
        super.onPause()
        if (heightProvider!!.isSoftInputVisible) {
            KeyboardUtils.hideSoftInput(requireActivity())
        }
    }

    override fun onResume() {
        super.onResume()
        AppManager.iCloudMessageManager.markC2CMessageAsRead(userId!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.localEventLifecycleViewModel.unRegister(this)
    }

    override fun onItemClick(itemView: View, item: Message, position: Int?) {
        when (itemView.id) {
            R.id.left_message_avatar, R.id.right_message_avatar -> {
                showToast("头像")
            }
            R.id.left_msg_text, R.id.right_msg_text -> {
                showToast("文本")
            }
            R.id.iv_send_failed -> {
                showToast("重发")
                viewModel.resendMessage(item, userId!!, messageList, position!!)
            }
            else -> {
                if (heightProvider!!.isSoftInputVisible) {
                    KeyboardUtils.hideSoftInput(requireActivity())
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
                        messageType = event.msg.elemType,
                        messageTime = TimeUtils.date2String(TimeUtils.millis2Date(event.msg.timestamp * 1000)),
                        messageSender = event.msg.sender == AppManager.currentUserID,
                        showTime = false,
                        loading = event.msg.status == V2TIMMessage.V2TIM_MSG_STATUS_SENDING,
                        sendFailed = event.msg.status == com.tencent.imsdk.message.Message.V2TIM_MSG_STATUS_SEND_FAILED,
                        v2TIMMessage = event.msg
                    )
                    messageList.add(0, message)
                    mChatListAdapter.resetItems(messageList)
                    AppManager.iCloudMessageManager.markC2CMessageAsRead(userId!!)
                }
            }
            else -> {

            }
        }
    }
}