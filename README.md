Camera框架

* ##框架说明

​	目前框架中集成了camera1、camera2、uvc三种相机的使用，三种相机方式均可设置视频预览的尺寸，其中camera1、camera2还支持白平衡、HDR、闪光灯、前后置等设置方法，**uvc相机对以上几个特性暂未添加支持**

* ##基础使用

  * 相机权限声明

    ```
     <uses-permission android:name="android.permission.CAMERA"/>
     <uses-feature android:name="android.hardware.camera"/>
     <uses-feature android:name="android.hardware.camera.autofocus"/>
     <uses-feature android:name="android.hardware.camera.flash"/>

    ```

    - Android6.0以下即在AndroidManifest.xml中权限声明即可，在6.0以上的系统需进行相机动态权限的声明(相机权限为危险权限)

  * 布局添加文件

    ```java
    <com.core.camera.CameraView
            android:id="@+id/camera"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:layout_gravity="center"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintHeight_percent="0.5"
            android:keepScreenOn="true"
            app:cameraApiType="camera_2"
            app:cameraWhiteBalance="auto"
            app:cameraFacing="front"
            app:cameraPreviewSize="v1080p"
            app:cameraFlash="off"/>
    ```

    **cameraApiType(相机类型)**：camera_1-Camera1、camera2-Camera2、camera_3-uvc

    **cameraWhiteBalance(相机白平衡)**：auto、incandescent、fluorescent、daylight、cloudy

    **cameraFacing(前后置)**：front、back

    **cameraFlash(闪光灯)**：off、on、auto、torch

    **cameraPreviewSize(预览尺寸)**：v640p、v720p、v1080p


  * 注册生命周期

    ` camera.setLifecycleOwner(this)`

    **需将该控件实例化后，在Activity或事Fragment中注册当前页面的生命周期回调，注册完成之后，即可实现相机的自动开关操作**

* ##进阶使用

  * 数据预览回调

    ```java
    public void setCameraCallback(CameraCallback cameraCallback)
    ```

    ```Java
    public interface CameraCallback {
      	//Frame(byte[] data, int width, int height, int format)
        void dispathFrame(Frame frame);
    }
    ```

  * 切换前后置

    ```Java
    /**
     * switch cameraID
     * It is used camera1、camera2
     */
    public void switchCamera()
    ```

  * 前后置设置

    ```java
    /**
     * @param facing cameraId
     * @see Facing#FRONT
     * @see Facing#BACK
     */
    public void setFacing(Facing facing)
    ```

  * 白平衡设置

    ```java
    /**
         * @param whiteBalance Whitebalance mode
         * @see WhiteBalance#AUTO
         * @see WhiteBalance#CLOUDY
         * @see WhiteBalance#DAYLIGHT
         * @see WhiteBalance#FLUORESCENT
         * @see WhiteBalance#INCANDESCENT
         */
        public void setWhiteBalance(WhiteBalance whiteBalance)
    ```

  * Hdr设置

    ```java
    /**
     * @param hdr hdr mode
     * @see Hdr#OFF
     * @see Hdr#ON
     */
    public void setHdr(Hdr hdr)
    ```

  * 闪光灯设置

    ```java
    /**
     * @param flash Flash mode
     * @see Flash#OFF
     * @see Flash#ON
     * @see Flash#AUTO
     * @see Flash#TORCH
     */
    public void setFlash(Flash flash)
    ```

  * 预览尺寸设置

    ```java
    /**
     * @param previewSize Preview Size
     * @see PreviewSize#V480P
     * @see PreviewSize#V720P
     * @see PreviewSize#V1080P
     */
    public void setPreviewSize(PreviewSize previewSize)
    ```