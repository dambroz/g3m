package org.glob3.mobile.generated; 
//
//  PyramidDEMProvider.cpp
//  G3MiOSSDK
//
//  Created by Diego Gomez Deck on 10/19/16.
//
//

//
//  PyramidDEMProvider.hpp
//  G3MiOSSDK
//
//  Created by Diego Gomez Deck on 10/19/16.
//
//





//class DEMGrid;
//class PyramidNode;


public abstract class PyramidDEMProvider extends DEMProvider
{

  private final int _rootNodesCount;
  private java.util.ArrayList<PyramidNode> _rootNodes;

  private java.util.ArrayList<PyramidNode> getRootNodes()
  {
    if (_rootNodes == null)
    {
      _rootNodes = new java.util.ArrayList<PyramidNode>();
      for (int i = 0; i < _rootNodesCount; i++)
      {
        PyramidNode rootNode = createNode(null, i);
        _rootNodes.add(rootNode);
      }
    }
    return _rootNodes;
  }


  private static class Subscription
  {
    private final Sector _sector ;
    private final Vector2I _extent = new Vector2I();
    private final Geodetic2D _resolution ;

    private DEMListener _listener;
    private final boolean _deleteListener;

    public Subscription(Sector sector, Vector2I extent, DEMListener listener, boolean deleteListener)
    {
       _sector = new Sector(sector);
       _extent = new Vector2I(extent);
       _resolution = new Geodetic2D(sector._deltaLatitude.div(extent._y), sector._deltaLongitude.div(extent._x));
       _listener = listener;
       _deleteListener = deleteListener;
    }

    public void dispose()
    {
      if (_deleteListener)
      {
        if (_listener != null)
           _listener.dispose();
      }
    }

  }



  protected final void insertGrid(int z, int x, int y, DEMGrid grid, boolean stickyGrid)
  {
    java.util.ArrayList<PyramidNode> rootNodes = getRootNodes();
    for (int i = 0; i < _rootNodesCount; i++)
    {
      PyramidNode rootNode = rootNodes.get(i);
      if (rootNode.insertGrid(z, x, y, grid, stickyGrid, this))
      {
        return;
      }
    }
    throw new RuntimeException("can't insert grid");
  }

  protected PyramidDEMProvider(double deltaHeight, int rootNodesCount)
  {
     super(deltaHeight);
     _rootNodesCount = rootNodesCount;
     _rootNodes = null;
  }

  public void dispose()
  {
    if (_rootNodes != null)
    {
      for (int i = 0; i < _rootNodesCount; i++)
      {
        PyramidNode rootNode = _rootNodes.get(i);
        if (rootNode != null)
           rootNode.dispose();
      }
      _rootNodes = null;
    }
    super.dispose();
  }



  public abstract PyramidNode createNode(PyramidNode parent, int childID);

  public final long subscribe(Sector sector, Vector2I extent, DEMListener listener, boolean deleteListener)
  {
  //  Subscription* subscription = new Subscription(sector, extent, listener, deleteListener);
    throw new RuntimeException("Not yet done");
  }

  public final void unsubscribe(long subscriptionID)
  {
    throw new RuntimeException("Not yet done");
  }

}