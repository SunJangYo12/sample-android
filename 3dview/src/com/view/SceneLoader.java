package com.view;

import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import com.view.lib.engine.animation.Animator;
import com.view.lib.engine.collision.CollisionDetection;
import com.view.lib.engine.model.Camera;
import com.view.lib.engine.services.Object3DBuilder;
import com.view.lib.engine.model.Object3DData;
import com.view.lib.engine.services.LoaderTask;
import com.view.lib.engine.services.collada.ColladaLoaderTask;
import com.view.lib.engine.services.stl.STLLoaderTask;
import com.view.lib.engine.services.wavefront.WavefrontLoaderTask;

import com.view.lib.util.android.ContentUtils;
import com.view.lib.util.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class loads a 3D scena as an example of what can be done with the app
 *
 * @author andresoviedo
 */
public class SceneLoader implements LoaderTask.Callback {

    /**
     * Default model color: yellow
     */
    private static float[] DEFAULT_COLOR = {1.0f, 1.0f, 0, 1.0f};
    protected final MainActivity parent;
    protected final Context context;
    private List<Object3DData> objects = new ArrayList<Object3DData>();
    private Camera camera;
    private boolean drawWireframe = false;
    private boolean drawingPoints = false;
    private boolean drawBoundingBox = false;
    private boolean drawNormals = false;
    private boolean drawTextures = true;
    private boolean rotatingLight = true;
    private boolean drawLighting = true;
    private boolean animateModel = true;
    private boolean drawSkeleton = false;
    private boolean isCollision = false;
    private boolean isAnaglyph = false;
    private Object3DData selectedObject = null;
    private final float[] lightPosition = new float[]{0, 0, 6, 1};
    private final Object3DData lightPoint = Object3DBuilder.buildPoint(lightPosition).setId("light");
    private Animator animator = new Animator();
    private boolean userHasInteracted;
    private long startTime;

    public SceneLoader(MainActivity main, Context context) {
        this.parent = main;
        this.context = context;
    }

    public void init() {
        // Camera to show a point of view
        camera = new Camera();

        if (parent.getParamUri() == null){
            return;
        }

        startTime = SystemClock.uptimeMillis();
        Uri uri = parent.getParamUri();
        Log.i("Object3DBuilder", "Loading model " + uri + ". async and parallel..");
        if (uri.toString().toLowerCase().endsWith(".obj") || parent.getParamType() == 0) {
            new WavefrontLoaderTask(uri, this).execute();
        
        } else if (uri.toString().toLowerCase().endsWith(".stl") || parent.getParamType() == 1) {
            Log.i("Object3DBuilder", "Loading STL object from: "+uri);
            new STLLoaderTask(uri, this).execute();
        
        } else if (uri.toString().toLowerCase().endsWith(".dae") || parent.getParamType() == 2) {
            Log.i("Object3DBuilder", "Loading Collada object from: "+uri);
            new ColladaLoaderTask(uri, this).execute();
        }
    }

    public Camera getCamera() {
        return camera;
    }

    private void makeToastText(final String text, final int toastDuration) {
        //paToast.makeText(parent.getApplicationContext(), text, toastDuration).show());
    }

    public Object3DData getLightBulb() {
        return lightPoint;
    }

    public float[] getLightPosition() {
        return lightPosition;
    }

    /**
     * Hook for animating the objects before the rendering
     */
    public void onDrawFrame() {

        animateLight();

        // smooth camera transition
        camera.animate();

        // initial camera animation. animate if user didn't touch the screen
        if (!userHasInteracted) {
        }

        if (objects.isEmpty()) return;

        if (animateModel) {
            for (int i=0; i<objects.size(); i++) {
                Object3DData obj = objects.get(i);
                animator.update(obj);
            }
        }
    }

    private void animateLight() {
        if (!rotatingLight) return;

        // animate light - Do a complete rotation every 5 seconds.
        long time = SystemClock.uptimeMillis() % 5000L;
        float angleInDegrees = (360.0f / 5000.0f) * ((int) time);
        lightPoint.setRotationY(angleInDegrees);
    }

    private void animateCamera(){
        camera.translateCamera(0.0025f, 0f);
    }

    synchronized void addObject(Object3DData obj) {
        List<Object3DData> newList = new ArrayList<Object3DData>(objects);
        newList.add(obj);
        this.objects = newList;
        requestRender();
    }

    public void requestRender() {
        // request render only if GL view is already initialized
        if (parent.getGLView() != null) {
            parent.getGLView().requestRender();
        }
    }

    public synchronized List<Object3DData> getObjects() {
        return objects;
    }

    public void toggleWireframe() {
        if (this.drawWireframe && !this.drawingPoints) {
            this.drawWireframe = false;
            this.drawingPoints = true;
            makeToastText("Points", Toast.LENGTH_SHORT);
        } else if (this.drawingPoints) {
            this.drawingPoints = false;
            makeToastText("Faces", Toast.LENGTH_SHORT);
        } else {
            makeToastText("Wireframe", Toast.LENGTH_SHORT);
            this.drawWireframe = true;
        }
        requestRender();
    }

    public boolean isDrawWireframe() {
        return this.drawWireframe;
    }

    public boolean isDrawPoints() {
        return this.drawingPoints;
    }

    public void toggleBoundingBox() {
        this.drawBoundingBox = !drawBoundingBox;
        requestRender();
    }

    public boolean isDrawBoundingBox() {
        return drawBoundingBox;
    }

    public boolean isDrawNormals() {
        return drawNormals;
    }

    public void toggleTextures() {
        this.drawTextures = !drawTextures;
        makeToastText("Textures "+this.drawTextures, Toast.LENGTH_SHORT);
    }

    public void toggleLighting() {
        if (this.drawLighting && this.rotatingLight) {
            this.rotatingLight = false;
            makeToastText("Light stopped", Toast.LENGTH_SHORT);
        } else if (this.drawLighting && !this.rotatingLight) {
            this.drawLighting = false;
            makeToastText("Lights off", Toast.LENGTH_SHORT);
        } else {
            this.drawLighting = true;
            this.rotatingLight = true;
            makeToastText("Light on", Toast.LENGTH_SHORT);
        }
        requestRender();
    }

    public void toggleAnimation() {
        if (animateModel && !drawSkeleton){
            this.drawSkeleton = true;
            makeToastText("Skeleton on", Toast.LENGTH_SHORT);
        } else if (animateModel){
            this.drawSkeleton = false;
            this.animateModel = false;
            makeToastText("Animation off", Toast.LENGTH_SHORT);
        } else {
            animateModel = true;
            makeToastText("Animation on", Toast.LENGTH_SHORT);
        }
    }

    public boolean isDrawAnimation() {
        return animateModel;
    }

    public void toggleCollision() {
        this.isCollision = !isCollision;
        makeToastText("Collisions: "+isCollision, Toast.LENGTH_SHORT);
    }

    public boolean isDrawTextures() {
        return drawTextures;
    }

    public boolean isDrawLighting() {
        return drawLighting;
    }

    public boolean isDrawSkeleton() {
        return drawSkeleton;
    }

    public boolean isCollision() {
        return isCollision;
    }

    public boolean isAnaglyph() {
        return isAnaglyph;
    }

    @Override
    public void onStart(){
        ContentUtils.setThreadActivity(context);
    }

    @Override
    public void onLoadComplete(List<Object3DData> datas) {
        // TODO: move texture load to LoaderTask
        for (Object3DData data : datas) {
            if (data.getTextureData() == null && data.getTextureFile() != null) {
                Log.i("LoaderTask","Loading texture... "+data.getTextureFile());
                try (InputStream stream = ContentUtils.getInputStream(data.getTextureFile())){
                    if (stream != null) {
                        data.setTextureData(IOUtils.read(stream));
                    }
                } catch (IOException ex) {
                    data.addError("Problem loading texture " + data.getTextureFile());
                }
            }
        }
        // TODO: move error alert to LoaderTask
        List<String> allErrors = new ArrayList<>();
        for (Object3DData data : datas) {
            addObject(data);
            allErrors.addAll(data.getErrors());
        }
        if (!allErrors.isEmpty()){
            makeToastText(allErrors.toString(), Toast.LENGTH_LONG);
        }
        final String elapsed = (SystemClock.uptimeMillis() - startTime) / 1000 + " secs";
        makeToastText("Build complete (" + elapsed + ")", Toast.LENGTH_LONG);
        ContentUtils.setThreadActivity(null);
    }

    @Override
    public void onLoadError(Exception ex) {
        Log.e("SceneLoader", ex.getMessage(), ex);
        makeToastText("There was a problem building the model: " + ex.getMessage(), Toast.LENGTH_LONG);
        ContentUtils.setThreadActivity(null);
    }

    public Object3DData getSelectedObject() {
        return selectedObject;
    }

    private void setSelectedObject(Object3DData selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void loadTexture(Object3DData obj, Uri uri) throws IOException {
        if (obj == null && objects.size() != 1) {
            makeToastText("Unavailable", Toast.LENGTH_SHORT);
            return;
        }
        obj = obj != null ? obj : objects.get(0);
        obj.setTextureData(IOUtils.read(ContentUtils.getInputStream(uri)));
        this.drawTextures = true;
    }

    public void processTouch(float x, float y) {
        ModelRenderer mr = parent.getGLView().getModelRenderer();
        Object3DData objectToSelect = CollisionDetection.getBoxIntersection(getObjects(), mr.getWidth(), mr.getHeight
                (), mr.getModelViewMatrix(), mr.getModelProjectionMatrix(), x, y);
        if (objectToSelect != null) {
            if (getSelectedObject() == objectToSelect) {
                Log.i("SceneLoader", "Unselected object " + objectToSelect.getId());
                setSelectedObject(null);
            } else {
                Log.i("SceneLoader", "Selected object " + objectToSelect.getId());
                setSelectedObject(objectToSelect);
            }
            if (isCollision()) {
                Log.d("SceneLoader", "Detecting collision...");

                float[] point = CollisionDetection.getTriangleIntersection(getObjects(), mr.getWidth(), mr.getHeight
                        (), mr.getModelViewMatrix(), mr.getModelProjectionMatrix(), x, y);
                if (point != null) {
                    Log.i("SceneLoader", "Drawing intersection point: " + Arrays.toString(point));
                    addObject(Object3DBuilder.buildPoint(point).setColor(new float[]{1.0f, 0f, 0f, 1f}));
                }
            }
        }
    }

    public void processMove(float dx1, float dy1) {
        userHasInteracted = true;
    }


}
