package com.mainli.activity.gl

import android.app.Activity
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import com.seekting.demo_lib.Demo
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
@Demo(title = "Hello OpenGL", group = ["OpenGL"])
class HelloPointsActivity : Activity() {
    private lateinit var glSurfaceView: GLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Play with Points"
        glSurfaceView = GLSurfaceView(this)
        setContentView(glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(PointsRender)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    object PointsRender : GLSurfaceView.Renderer {


        private const val VERTEX_SHADER = "void main() {\n" +
                "gl_Position = vec4(0.5, -0.5, 0.0, 1.0);\n" +
                "gl_PointSize = 120.0;\n" + "}\n"


        private const val FRAGMENT_SHADER =
                "void main() {\n" +
                        "gl_FragColor = vec4(0., 0, 1.0, 1.0);\n" + "}\n"

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)// 清除颜色缓冲区，因为我们要开始新一帧的绘制了，所以先清理，以免有脏数据。
            GLES20.glUseProgram(mGLProgram)// 告诉OpenGL，使用我们在onSurfaceCreated里面准备好了的shader program来渲染
            // 开始渲染，发送渲染点的指令， 第二个参数是offset，第三个参数是点的个数。目前只有一个点，所以是1。
            GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)// 参数是left, top, width, height
        }

        var mGLProgram = 1
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES20.glClearColor(199f / 255, 237f / 255, 204f / 255, 1f)

            val vertex_shader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
            GLES20.glShaderSource(vertex_shader, VERTEX_SHADER)
            GLES20.glCompileShader(vertex_shader)

            val fragment_shader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
            GLES20.glShaderSource(fragment_shader, FRAGMENT_SHADER)
            GLES20.glCompileShader(fragment_shader)

            mGLProgram = GLES20.glCreateProgram()// 创建shader program句柄
            GLES20.glAttachShader(mGLProgram, vertex_shader)// 把vertex shader添加到program
            GLES20.glAttachShader(mGLProgram, fragment_shader)// 把fragment shader添加到program
            GLES20.glLinkProgram(mGLProgram)// 做链接，可以理解为把两种shader进行融合，做好投入使用的最后准备工作

            /**
             *    检测shader编译或者链接过程
             */
            GLES20.glValidateProgram(mGLProgram)// 让OpenGL来验证一下我们的shader program，并获取验证的状态
            val status = IntArray(1)
            GLES20.glGetProgramiv(mGLProgram, GLES20.GL_VALIDATE_STATUS, status, 0)
            Log.i("Mainli", "status:${Arrays.toString(status)}")
            GLES20.glGetProgramInfoLog(mGLProgram)
        }

    }
}