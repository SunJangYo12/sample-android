package com.view;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import android.graphics.PointF;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.widget.ImageView;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.view.lib.engine.animation.Animator;
import com.view.lib.engine.drawer.DrawerFactory;
import com.view.lib.engine.model.Camera;
import com.view.lib.engine.model.AnimatedModel;
import com.view.lib.engine.model.Object3D;
import com.view.lib.engine.services.Object3DBuilder;
import com.view.lib.engine.model.Object3DData;
import com.view.lib.engine.drawer.Object3DImpl;
import com.view.lib.util.android.GLUtil;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * This is the actual opengl view. From here we can detect touch gestures for example
 * 
 * @author andresoviedo
 *
 */
public class ModelSurfaceView extends GLSurfaceView {

	private ModelRenderer mRenderer;
	private TouchController touchHandler;

	public ModelSurfaceView(MainActivity parrent) {
		super(parrent);

		setEGLContextClientVersion(2);

		// This is the actual renderer of the 3D space
		mRenderer = new ModelRenderer(parrent);
		setRenderer(mRenderer);

		touchHandler = new TouchController(parrent, mRenderer);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return touchHandler.onTouchEvent(event);
	}

	public ModelRenderer getModelRenderer(){
		return mRenderer;
	}

	
}

class ModelRenderer implements GLSurfaceView.Renderer {

	private final static String TAG = ModelRenderer.class.getName();
	private MainActivity main;
	private int width;
	private int height;
	private static final float near = 1f;
	private static final float far = 100f;
	private DrawerFactory drawer;
	private Map<Object3DData, Object3DData> wireframes = new HashMap<Object3DData, Object3DData>();
	private Map<byte[], Integer> textures = new HashMap<byte[], Integer>();
	private Map<Object3DData, Object3DData> boundingBoxes = new HashMap<Object3DData, Object3DData>();
	private Map<Object3DData, Object3DData> normals = new HashMap<Object3DData, Object3DData>();
	private Map<Object3DData, Object3DData> skeleton = new HashMap<>();
	private final float[] modelProjectionMatrix = new float[16];
	private final float[] modelViewMatrix = new float[16];
	private final float[] mvpMatrix = new float[16];
	private final float[] lightPosInEyeSpace = new float[4];
	private boolean infoLogged = false;
	private Animator animator = new Animator();

	public ModelRenderer(MainActivity main) {
		this.main = main;
	}

	
	public float getNear() {
		return near;
	}

	public float getFar() {
		return far;
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		float[] backgroundColor = main.getBackgroundColor();
		GLES20.glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], backgroundColor[3]);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		drawer = new DrawerFactory();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		this.width = width;
		this.height = height;

		GLES20.glViewport(0, 0, width, height);

		SceneLoader scene = main.getScene();
		Camera camera = scene.getCamera();
		Matrix.setLookAtM(modelViewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
				camera.zView, camera.xUp, camera.yUp, camera.zUp);

		// the projection matrix is the 3D virtual space (cube) that we want to project
		float ratio = (float) width / height;
		Log.d(TAG, "projection: [" + -ratio + "," + ratio + ",-1,1]-near/far[1,10]");
		Matrix.frustumM(modelProjectionMatrix, 0, -ratio, ratio, -1, 1, getNear(), getFar());

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mvpMatrix, 0, modelProjectionMatrix, 0, modelViewMatrix, 0);
	}

	@Override
	public void onDrawFrame(GL10 unused) {

		// Draw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		SceneLoader scene = main.getScene();
		if (scene == null) {
			// scene not ready
			return;
		}

		// recalculate mvp matrix according to where we are looking at now
		Camera camera = scene.getCamera();
		camera.translateCamera(main.tes, 0);

		if (camera.hasChanged()) 
		{
			Matrix.setLookAtM(modelViewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView, camera.zView, camera.xUp, camera.yUp, camera.zUp);
			Matrix.multiplyMM(mvpMatrix, 0, modelProjectionMatrix, 0, modelViewMatrix, 0);
			camera.setChanged(false);
		}

		// draw light
		if (scene.isDrawLighting()) {

			Object3DImpl lightBulbDrawer = (Object3DImpl) drawer.getPointDrawer();

			float[] lightModelViewMatrix = lightBulbDrawer.getMvMatrix(lightBulbDrawer.getMMatrix(scene.getLightBulb()),modelViewMatrix);

			// Calculate position of the light in eye space to support lighting
			Matrix.multiplyMV(lightPosInEyeSpace, 0, lightModelViewMatrix, 0, scene.getLightPosition(), 0);

			// Draw a point that represents the light bulb
			lightBulbDrawer.draw(scene.getLightBulb(), modelProjectionMatrix, modelViewMatrix, -1, lightPosInEyeSpace);
		}

		List<Object3DData> objects = scene.getObjects();

		for (int i=0; i<objects.size(); i++) {
			Object3DData objData = null;
			try {
				objData = objects.get(i);
				boolean changed = objData.isChanged();

				Object3D drawerObject = drawer.getDrawer(objData, scene.isDrawTextures(), scene.isDrawLighting(),
                        scene.isDrawAnimation());

				if (!infoLogged) {
					Log.i("ModelRenderer","Using drawer "+drawerObject.getClass());
					infoLogged = true;
				}

				Integer textureId = textures.get(objData.getTextureData());
				if (textureId == null && objData.getTextureData() != null) {
					Log.i("ModelRenderer","Loading GL Texture...");
					ByteArrayInputStream textureIs = new ByteArrayInputStream(objData.getTextureData());
					textureId = GLUtil.loadTexture(textureIs);
					textureIs.close();
					textures.put(objData.getTextureData(), textureId);
				}

				if (objData.getDrawMode() == GLES20.GL_POINTS){
					Object3DImpl lightBulbDrawer = (Object3DImpl) drawer.getPointDrawer();
					lightBulbDrawer.draw(objData,modelProjectionMatrix, modelViewMatrix, GLES20.GL_POINTS,lightPosInEyeSpace);
				
				} else if (scene.isAnaglyph()){
				// TODO: implement anaglyph
				
				} else if (scene.isDrawWireframe() && objData.getDrawMode() != GLES20.GL_POINTS && objData.getDrawMode() != GLES20.GL_LINES && objData.getDrawMode() != GLES20.GL_LINE_STRIP && objData.getDrawMode() != GLES20.GL_LINE_LOOP) {
					// Log.d("ModelRenderer","Drawing wireframe model...");
					try{
						// Only draw wireframes for objects having faces (triangles)
						Object3DData wireframe = wireframes.get(objData);
						if (wireframe == null || changed) {
							Log.i("ModelRenderer","Generating wireframe model...");
							wireframe = Object3DBuilder.buildWireframe(objData);
							wireframes.put(objData, wireframe);
						}
						drawerObject.draw(wireframe,modelProjectionMatrix,modelViewMatrix,wireframe.getDrawMode(),
								wireframe.getDrawSize(),textureId != null? textureId:-1, lightPosInEyeSpace);
					}catch(Error e){
						Log.e("ModelRenderer",e.getMessage(),e);
					}
				
				} else if (scene.isDrawPoints() || objData.getFaces() == null || !objData.getFaces().loaded()) {
					drawerObject.draw(objData, modelProjectionMatrix, modelViewMatrix, GLES20.GL_POINTS, objData.getDrawSize(), textureId != null ? textureId : -1, lightPosInEyeSpace);
				
				} else if (scene.isDrawSkeleton() && objData instanceof AnimatedModel && ((AnimatedModel) objData).getAnimation() != null){
					Object3DData skeleton = this.skeleton.get(objData);
					if (skeleton == null){
						skeleton = Object3DBuilder.buildSkeleton((AnimatedModel) objData);
						this.skeleton.put(objData, skeleton);
					}
					animator.update(skeleton);
					drawerObject = drawer.getDrawer(skeleton, false, scene.isDrawLighting(), scene.isDrawAnimation());
					drawerObject.draw(skeleton, modelProjectionMatrix, modelViewMatrix,-1, lightPosInEyeSpace);
				
				} else {
					drawerObject.draw(objData, modelProjectionMatrix, modelViewMatrix, textureId != null ? textureId : -1, lightPosInEyeSpace);
				}

			} catch (Exception ex) {
				Log.e("ModelRenderer","There was a problem rendering the object '"+objData.getId()+"':"+ex.getMessage(),ex);
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float[] getModelProjectionMatrix() {
		return modelProjectionMatrix;
	}

	public float[] getModelViewMatrix() {
		return modelViewMatrix;
	}
}

class TouchController {

	private static final String TAG = TouchController.class.getName();

	private final MainActivity view;
	private final ModelRenderer mRenderer;

	private int pointerCount = 0;
    private float x1 = Float.MIN_VALUE;
    private float y1 = Float.MIN_VALUE;
    private float x2 = Float.MIN_VALUE;
    private float y2 = Float.MIN_VALUE;
    private float dx1 = Float.MIN_VALUE;
    private float dy1 = Float.MIN_VALUE;
    private float dx2 = Float.MIN_VALUE;
    private float dy2 = Float.MIN_VALUE;

    private float previousLength = Float.MIN_VALUE;
    private float currentPress1 = Float.MIN_VALUE;
    private float currentPress2 = Float.MIN_VALUE;

    private float rotation = 0;
    private int currentSquare = Integer.MIN_VALUE;

    private boolean isOneFixedAndOneMoving = false;
    private boolean fingersAreClosing = false;
    private boolean isRotating = false;

    private boolean gestureChanged = false;
	private boolean moving = false;
	private boolean simpleTouch = false;
	private long lastActionTime;
	private int touchDelay = -2;

	private float previousX1;
	private float previousY1;
	private float previousX2;
	private float previousY2;
    private float[] previousVector = new float[4];
    private float[] vector = new float[4];
    private float[] rotationVector = new float[4];
	private float previousRotationSquare;

	public TouchController(MainActivity view, ModelRenderer renderer) {
		super();
		this.view = view;
		this.mRenderer = renderer;
	}

	public synchronized boolean onTouchEvent(MotionEvent motionEvent) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		switch (motionEvent.getActionMasked()) {
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_HOVER_EXIT:
		case MotionEvent.ACTION_OUTSIDE:
			// this to handle "1 simple touch"
			if (lastActionTime > SystemClock.uptimeMillis() - 250) {
				simpleTouch = true;
			} else {
				gestureChanged = true;
				touchDelay = 0;
				lastActionTime = SystemClock.uptimeMillis();
				simpleTouch = false;
			}
			moving = false;
			break;
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_HOVER_ENTER:
			Log.d(TAG, "Gesture changed...");
			gestureChanged = true;
			touchDelay = 0;
			lastActionTime = SystemClock.uptimeMillis();
			simpleTouch = false;
			break;
		case MotionEvent.ACTION_MOVE:
			moving = true;
			simpleTouch = false;
			touchDelay++;
			break;
		default:
			Log.w(TAG, "Unknown state: " + motionEvent.getAction());
			gestureChanged = true;
		}

		pointerCount = motionEvent.getPointerCount();

		if (pointerCount == 1) {
			x1 = motionEvent.getX();
			y1 = motionEvent.getY();
			if (gestureChanged) {
				previousX1 = x1;
				previousY1 = y1;
			}
			dx1 = x1 - previousX1;
			dy1 = y1 - previousY1;

			Log.d(TAG, "x:" + dx1 + ",y:" + dy1);

		}

		int max = Math.max(mRenderer.getWidth(), mRenderer.getHeight());

		if (touchDelay > 1) {
			// INFO: Process gesture
            SceneLoader scene = view.getScene();
            scene.processMove(dx1, dy1);
			Camera camera = scene.getCamera();
			
			if (pointerCount == 1) {
				dx1 = (float)(dx1 / max * Math.PI * 2);
				dy1 = (float)(dy1 / max * Math.PI * 2);
				camera.translateCamera(dx1,dy1);
			
			} else if (pointerCount == 2) {
				if (fingersAreClosing) {
					float zoomFactor = (Float.MIN_VALUE - Float.MIN_VALUE) / max * mRenderer.getFar();
					Log.i(TAG, "Zooming '" + zoomFactor + "'...");
					camera.MoveCameraZ(zoomFactor);
				}
				if (isRotating) {
					Log.i(TAG, "Rotating camera '" + Math.signum(rotationVector[2]) + "'...");
					camera.Rotate((float) (Math.signum(rotationVector[2]) / Math.PI) / 4);
				}
			}

		}

		previousX1 = x1;
		previousY1 = y1;
		previousX2 = x2;
		previousY2 = y2;

		previousRotationSquare = currentSquare;

		System.arraycopy(vector, 0, previousVector, 0, vector.length);

		if (gestureChanged && touchDelay > 1) {
			gestureChanged = false;
			Log.v(TAG, "Fin");
		}

		view.getScene().requestRender();

		return true;

	}
}
