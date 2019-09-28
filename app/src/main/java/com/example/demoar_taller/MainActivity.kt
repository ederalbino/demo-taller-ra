package com.example.demoar_taller

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.TransformableNode

class MainActivity : AppCompatActivity() {

    private var TAG:String?="MiClassPrincipal"
    private var MIN_OPENGL_VERSION:Double=3.0
    private lateinit var arCoreFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!verificarDispositivo(this)) {
            return
        }
        setContentView(R.layout.activity_main)

        arCoreFragment = supportFragmentManager.findFragmentById(R.id.main_fragment) as ArFragment
        arCoreFragment.setOnTapArPlaneListener{ hitResult, plane, MotionEvent ->
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                //return for the callback
                return@setOnTapArPlaneListener
            }
            val anchor = hitResult.createAnchor()
            establecerObjeto(arCoreFragment,anchor)
        }
    }

    private fun establecerObjeto(fragment: ArFragment,anchor: Anchor){
        val modelRenderable = ModelRenderable.builder()
            .setSource(arCoreFragment.requireContext(), R.raw.dinosaur)
            .build()
        //when the model render is build add node to scene
        modelRenderable.thenAccept { renderableObject -> agregarNodo(fragment, anchor, renderableObject) }
        //handle error
        modelRenderable.exceptionally {
            val toast = Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
            toast.show()
            null
        }
    }

    private fun agregarNodo(fragment: ArFragment, anchor: Anchor, renderableObject: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.renderable = renderableObject
        transformableNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }

    private fun verificarDispositivo(activity:Activity):Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Version de Android no Compatible")
            activity.finish()
            return false
        }
        val openGlVersionString = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo.glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "La version minima de OpenGL es 3.0")
            activity.finish()
            return false
        }
        return true
    }
}
