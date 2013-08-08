package org.glob3.mobile.generated; 
//const URL MapBooBuilder::createApplicationDescriptionURL() const {
//  const std::string serverPath = _serverURL.getPath();
//
//  return URL(serverPath + "/application/" + _applicationId + "/runtime", false);
//}


public class MapBooBuilder_TubeWatchdogPeriodicalTask extends GTask
{
  private MapBooBuilder _builder;
  private boolean _firstRun;

  public MapBooBuilder_TubeWatchdogPeriodicalTask(MapBooBuilder builder)
  {
     _builder = builder;
     _firstRun = true;
  }

  public final void run(G3MContext context)
  {
    if (_firstRun)
    {
      _firstRun = false;
    }
    else
    {
      if (!_builder.isApplicationTubeOpen())
      {
        _builder.openApplicationTube(context);
      }
    }
  }

}