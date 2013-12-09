package org.glob3.mobile.generated; 
public class MeshRenderer extends LeafRenderer
{


  private static class LoadQueueItem
  {
    public final URL          _url;
    public final TimeInterval _timeToCache;
    public final long _priority;
    public final boolean _readExpired;
    public final float _pointSize;
    public final double _deltaHeight;
    public final Color _color;
    public MeshLoadListener _listener;
    public final boolean _deleteListener;
    public final boolean _isBSON;
    public final MeshType _meshType;

    public LoadQueueItem(URL url, long priority, TimeInterval timeToCache, boolean readExpired, float pointSize, double deltaHeight, Color color, MeshLoadListener listener, boolean deleteListener, boolean isBSON, MeshType meshType)
    {
       _url = url;
       _priority = priority;
       _timeToCache = timeToCache;
       _readExpired = readExpired;
       _pointSize = pointSize;
       _deltaHeight = deltaHeight;
       _color = color;
       _listener = listener;
       _deleteListener = deleteListener;
       _isBSON = isBSON;
       _meshType = meshType;

    }

    public void dispose()
    {
    }
  }



  private java.util.ArrayList<Mesh> _meshes = new java.util.ArrayList<Mesh>();

  private GLState _glState;
  private void updateGLState(G3MRenderContext rc)
  {
    final Camera cam = rc.getCurrentCamera();
  
    ModelViewGLFeature f = (ModelViewGLFeature) _glState.getGLFeature(GLFeatureID.GLF_MODEL_VIEW);
    if (f == null)
    {
      _glState.addGLFeature(new ModelViewGLFeature(cam), true);
    }
    else
    {
      f.setMatrix(cam.getModelViewMatrix44D());
    }
  }

  private G3MContext _context;

  private java.util.ArrayList<LoadQueueItem> _loadQueue = new java.util.ArrayList<LoadQueueItem>();

  private void drainLoadQueue()
  {
    final int loadQueueSize = _loadQueue.size();
    for (int i = 0; i < loadQueueSize; i++)
    {
      LoadQueueItem item = _loadQueue.get(i);
      requestMeshBuffer(item._url, item._priority, item._timeToCache, item._readExpired, item._pointSize, item._deltaHeight, item._color, item._listener, item._deleteListener, item._isBSON, item._meshType);
  
      if (item != null)
         item.dispose();
    }
  
    _loadQueue.clear();
  }

  private void requestMeshBuffer(URL url, long priority, TimeInterval timeToCache, boolean readExpired, float pointSize, double deltaHeight, Color color, MeshLoadListener listener, boolean deleteListener, boolean isBSON, MeshType meshType)
  {
    IDownloader downloader = _context.getDownloader();
    downloader.requestBuffer(url, priority, timeToCache, readExpired, new MeshRenderer_MeshBufferDownloadListener(this, pointSize, deltaHeight, color, listener, deleteListener, _context.getThreadUtils(), isBSON, meshType, _context), true);
  
  }

  private boolean _showNormals;



  public MeshRenderer()
  {
     _glState = new GLState();
     _context = null;
     _showNormals = false;
  }

  public void dispose()
  {
    final int meshesCount = _meshes.size();
    for (int i = 0; i < meshesCount; i++)
    {
      Mesh mesh = _meshes.get(i);
      if (mesh != null)
         mesh.dispose();
    }
  
    _glState._release();
  
    super.dispose();
  }

  public final void addMesh(Mesh mesh)
  {
    _meshes.add(mesh);
    mesh.showNormals(_showNormals);
  }

  public final void clearMeshes()
  {
    final int meshesCount = _meshes.size();
    for (int i = 0; i < meshesCount; i++)
    {
      Mesh mesh = _meshes.get(i);
      if (mesh != null)
         mesh.dispose();
    }
    _meshes.clear();
  }

  public final void enableAll()
  {
    final int meshesCount = _meshes.size();
    for (int i = 0; i < meshesCount; i++)
    {
      Mesh mesh = _meshes.get(i);
      mesh.setEnable(true);
    }
  }

  public final void disableAll()
  {
    final int meshesCount = _meshes.size();
    for (int i = 0; i < meshesCount; i++)
    {
      Mesh mesh = _meshes.get(i);
      mesh.setEnable(false);
    }
  }

  public final void onResume(G3MContext context)
  {

  }

  public final void onPause(G3MContext context)
  {

  }

  public final void onDestroy(G3MContext context)
  {

  }

  public final void initialize(G3MContext context)
  {
    _context = context;
  
    if (_context != null)
    {
      drainLoadQueue();
    }
  }

  public final RenderState getRenderState(G3MRenderContext rc)
  {
    return RenderState.ready();
  }

  public final void render(G3MRenderContext rc, GLState glState)
  {
    final Frustum frustum = rc.getCurrentCamera().getFrustumInModelCoordinates();
    updateGLState(rc);
  
    _glState.setParent(glState);
  
    final int meshesCount = _meshes.size();
    for (int i = 0; i < meshesCount; i++)
    {
      Mesh mesh = _meshes.get(i);
      final BoundingVolume boundingVolume = mesh.getBoundingVolume();
      if (boundingVolume.touchesFrustum(frustum))
      {
        mesh.render(rc, _glState);
      }
    }
  }

  public final boolean onTouchEvent(G3MEventContext ec, TouchEvent touchEvent)
  {
    return false;
  }

  public final void onResizeViewportEvent(G3MEventContext ec, int width, int height)
  {

  }

  public final void start(G3MRenderContext rc)
  {

  }

  public final void stop(G3MRenderContext rc)
  {

  }

  public final void loadJSONPointCloud(URL url, long priority, TimeInterval timeToCache, boolean readExpired, float pointSize, double deltaHeight, MeshLoadListener listener)
  {
     loadJSONPointCloud(url, priority, timeToCache, readExpired, pointSize, deltaHeight, listener, true);
  }
  public final void loadJSONPointCloud(URL url, long priority, TimeInterval timeToCache, boolean readExpired, float pointSize, double deltaHeight)
  {
     loadJSONPointCloud(url, priority, timeToCache, readExpired, pointSize, deltaHeight, null, true);
  }
  public final void loadJSONPointCloud(URL url, long priority, TimeInterval timeToCache, boolean readExpired, float pointSize)
  {
     loadJSONPointCloud(url, priority, timeToCache, readExpired, pointSize, 0, null, true);
  }
  public final void loadJSONPointCloud(URL url, long priority, TimeInterval timeToCache, boolean readExpired, float pointSize, double deltaHeight, MeshLoadListener listener, boolean deleteListener)
  {
    if (_context == null)
    {
      _loadQueue.add(new LoadQueueItem(url, priority, timeToCache, readExpired, pointSize, deltaHeight, null, listener, deleteListener, false, MeshType.POINT_CLOUD)); // isBson -  color
    }
    else
    {
      requestMeshBuffer(url, priority, timeToCache, readExpired, pointSize, deltaHeight, null, listener, deleteListener, false, MeshType.POINT_CLOUD); // isBson -  color
    }
  }

  public final void loadJSONPointCloud(URL url, float pointSize, double deltaHeight, MeshLoadListener listener)
  {
     loadJSONPointCloud(url, pointSize, deltaHeight, listener, true);
  }
  public final void loadJSONPointCloud(URL url, float pointSize, double deltaHeight)
  {
     loadJSONPointCloud(url, pointSize, deltaHeight, null, true);
  }
  public final void loadJSONPointCloud(URL url, float pointSize)
  {
     loadJSONPointCloud(url, pointSize, 0, null, true);
  }
  public final void loadJSONPointCloud(URL url, float pointSize, double deltaHeight, MeshLoadListener listener, boolean deleteListener)
  {
    loadJSONPointCloud(url, DownloadPriority.MEDIUM, TimeInterval.fromDays(30), true, pointSize, deltaHeight, listener, deleteListener);
  }

  public final void loadBSONPointCloud(URL url, long priority, TimeInterval timeToCache, boolean readExpired, float pointSize, double deltaHeight, MeshLoadListener listener)
  {
     loadBSONPointCloud(url, priority, timeToCache, readExpired, pointSize, deltaHeight, listener, true);
  }
  public final void loadBSONPointCloud(URL url, long priority, TimeInterval timeToCache, boolean readExpired, float pointSize, double deltaHeight)
  {
     loadBSONPointCloud(url, priority, timeToCache, readExpired, pointSize, deltaHeight, null, true);
  }
  public final void loadBSONPointCloud(URL url, long priority, TimeInterval timeToCache, boolean readExpired, float pointSize)
  {
     loadBSONPointCloud(url, priority, timeToCache, readExpired, pointSize, 0, null, true);
  }
  public final void loadBSONPointCloud(URL url, long priority, TimeInterval timeToCache, boolean readExpired, float pointSize, double deltaHeight, MeshLoadListener listener, boolean deleteListener)
  {
    if (_context == null)
    {
      _loadQueue.add(new LoadQueueItem(url, priority, timeToCache, readExpired, pointSize, deltaHeight, null, listener, deleteListener, true, MeshType.POINT_CLOUD)); // isBson -  color
    }
    else
    {
      requestMeshBuffer(url, priority, timeToCache, readExpired, pointSize, deltaHeight, null, listener, deleteListener, true, MeshType.POINT_CLOUD); // isBson -  color
    }
  }

  public final void loadBSONPointCloud(URL url, float pointSize, double deltaHeight, MeshLoadListener listener)
  {
     loadBSONPointCloud(url, pointSize, deltaHeight, listener, true);
  }
  public final void loadBSONPointCloud(URL url, float pointSize, double deltaHeight)
  {
     loadBSONPointCloud(url, pointSize, deltaHeight, null, true);
  }
  public final void loadBSONPointCloud(URL url, float pointSize)
  {
     loadBSONPointCloud(url, pointSize, 0, null, true);
  }
  public final void loadBSONPointCloud(URL url, float pointSize, double deltaHeight, MeshLoadListener listener, boolean deleteListener)
  {
    loadBSONPointCloud(url, DownloadPriority.MEDIUM, TimeInterval.fromDays(30), true, pointSize, deltaHeight, listener, deleteListener);
  }

  public final void zRender(G3MRenderContext rc, GLState glState)
  {
    final Camera cam = rc.getCurrentCamera();
    final Frustum frustum = rc.getCurrentCamera().getFrustumInModelCoordinates();
  
    GLState state = new GLState();
    state.addGLFeature(new ModelViewGLFeature(cam), true);
    state.setParent(glState);
  
    final int meshesCount = _meshes.size();
    for (int i = 0; i < meshesCount; i++)
    {
      Mesh mesh = _meshes.get(i);
      final BoundingVolume boundingVolume = mesh.getBoundingVolume();
      if (boundingVolume.touchesFrustum(frustum))
      {
        mesh.zRender(rc, state);
      }
    }
  
    state._release();
  
  }

  public final void loadJSONMesh(URL url, Color color, long priority, TimeInterval timeToCache, boolean readExpired, MeshLoadListener listener)
  {
     loadJSONMesh(url, color, priority, timeToCache, readExpired, listener, true);
  }
  public final void loadJSONMesh(URL url, Color color, long priority, TimeInterval timeToCache, boolean readExpired)
  {
     loadJSONMesh(url, color, priority, timeToCache, readExpired, null, true);
  }
  public final void loadJSONMesh(URL url, Color color, long priority, TimeInterval timeToCache, boolean readExpired, MeshLoadListener listener, boolean deleteListener)
  {
    if (_context == null)
    {
      _loadQueue.add(new LoadQueueItem(url, priority, timeToCache, readExpired, 1, 0, color, listener, deleteListener, false, MeshType.MESH)); // isBson -  deltaHeight -  pointSize
    }
    else
    {
      requestMeshBuffer(url, priority, timeToCache, readExpired, 1, 0, color, listener, deleteListener, false, MeshType.MESH); // isBson -  deltaHeight -  pointSize
    }
  }

  public final void loadJSONMesh(URL url, Color color, MeshLoadListener listener)
  {
     loadJSONMesh(url, color, listener, true);
  }
  public final void loadJSONMesh(URL url, Color color)
  {
     loadJSONMesh(url, color, null, true);
  }
  public final void loadJSONMesh(URL url, Color color, MeshLoadListener listener, boolean deleteListener)
  {
    loadJSONMesh(url, color, DownloadPriority.MEDIUM, TimeInterval.fromDays(30), true, listener, deleteListener);
  }

  public final void loadBSONMesh(URL url, Color color, long priority, TimeInterval timeToCache, boolean readExpired, MeshLoadListener listener)
  {
     loadBSONMesh(url, color, priority, timeToCache, readExpired, listener, true);
  }
  public final void loadBSONMesh(URL url, Color color, long priority, TimeInterval timeToCache, boolean readExpired)
  {
     loadBSONMesh(url, color, priority, timeToCache, readExpired, null, true);
  }
  public final void loadBSONMesh(URL url, Color color, long priority, TimeInterval timeToCache, boolean readExpired, MeshLoadListener listener, boolean deleteListener)
  {
    if (_context == null)
    {
      _loadQueue.add(new LoadQueueItem(url, priority, timeToCache, readExpired, 1, 0, color, listener, deleteListener, true, MeshType.MESH)); // isBson -  deltaHeight -  pointSize
    }
    else
    {
      requestMeshBuffer(url, priority, timeToCache, readExpired, 1, 0, color, listener, deleteListener, true, MeshType.MESH); // isBson -  deltaHeight -  pointSize
    }
  }

  public final void loadBSONMesh(URL url, Color color, MeshLoadListener listener)
  {
     loadBSONMesh(url, color, listener, true);
  }
  public final void loadBSONMesh(URL url, Color color)
  {
     loadBSONMesh(url, color, null, true);
  }
  public final void loadBSONMesh(URL url, Color color, MeshLoadListener listener, boolean deleteListener)
  {
    loadBSONMesh(url, color, DownloadPriority.MEDIUM, TimeInterval.fromDays(30), true, listener, deleteListener);
  }

  public final void showNormals(boolean v)
  {
    _showNormals = v;
    final int meshesCount = _meshes.size();
    for (int i = 0; i < meshesCount; i++)
    {
      Mesh mesh = _meshes.get(i);
      mesh.showNormals(v);
    }
  }


}