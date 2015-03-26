//
//  G3MOSMBuildingsDemoScene.cpp
//  G3MApp
//
//  Created by Pratik Prakash on 3/26/15.
//  Copyright (c) 2015 Igo Software SL. All rights reserved.
//

#include "G3MOSMBuildingsDemoScene.hpp"

#include <G3MiOSSDK/G3MWidget.hpp>
#include <G3MiOSSDK/LayerSet.hpp>
#include <G3MiOSSDK/IDownloader.hpp>
#include <G3MiOSSDK/DownloadPriority.hpp>
#include <G3MiOSSDK/IBufferDownloadListener.hpp>
#include <G3MiOSSDK/IJSONParser.hpp>
#include <G3MiOSSDK/JSONObject.hpp>
#include <G3MiOSSDK/JSONArray.hpp>
#include <G3MiOSSDK/JSONNumber.hpp>
#include <G3MiOSSDK/Mark.hpp>
#include <G3MiOSSDK/Geodetic3D.hpp>
#include <G3MiOSSDK/IStringUtils.hpp>
#include <G3MiOSSDK/MarksRenderer.hpp>
#include <G3MiOSSDK/BingMapsLayer.hpp>

#include "G3MDemoModel.hpp"


class G3MOSMBuildingsDemoScene_BufferDownloadListener : public IBufferDownloadListener {
private:
    G3MOSMBuildingsDemoScene* _scene;
public:
    G3MOSMBuildingsDemoScene_BufferDownloadListener(G3MOSMBuildingsDemoScene* scene) :
    _scene(scene)
    {
    }
    
    void onDownload(const URL& url,
                    IByteBuffer* buffer,
                    bool expired) {
        
        //Create the JSON parser
        IJSONParser* jParser = IJSONParser::instance();
        const JSONBaseObject* jsonBuildingData = jParser->parse(buffer);
        
        //Create a list of Geodetic
        std::vector<Geodetic3D*> coords;
        
        //Gets the type field from the JSON building data
        const JSONObject* buildings = jsonBuildingData->asObject();
        const JSONArray* features = buildings->getAsArray("features");
        for (int i=0; i < features->size(); i++) {
            const JSONObject* feature = features->getAsObject(i);
            const JSONObject* geometry = feature->getAsObject("geometry");
            const JSONArray* coordArray = geometry->getAsArray("coordinates");
            
            //TODO: get all the coordinates in geometry
            double lon = coordArray->getAsArray(0)->getAsArray(0)->getAsNumber(0, 0);
            double lat = coordArray->getAsArray(0)->getAsArray(0)->getAsNumber(1, 0);
            double height = 0;
            
            Geodetic3D tempCoord = Geodetic3D::fromDegrees(lat, lon, height);
            coords.push_back(&tempCoord);
            //std::string name = feature->getAsString("type", "");
            //TODO finish parsing all the other fields from building data
        }
        
        //TODO: Put a mark on all the coordinates in coords vector list
        for (int i = 0; i < coords.size(); i++) {
            
            URL iconurl = URL::URL("http://iconizer.net/files/Farm-fresh/orig/building.png");
            double minDistanceToCamera = 0;
            MarkUserData* userData = new MarkUserData::MarkUserData();
            bool autoDeleteUserData = true;
            MarkTouchListener* marksListener = NULL;
            bool autoDeleteListener = true;
            Mark* mark = new Mark(iconurl, *coords.at(i), ABSOLUTE, minDistanceToCamera, userData, autoDeleteUserData, marksListener, autoDeleteListener);
            
            _scene->addMark(mark);
        }
        
    }

    void onError(const URL& url) {
        ILogger::instance()->logError("Error downloading \"%s\"", url.getPath().c_str());
    }
    
    void onCancel(const URL& url) {
        // do nothing
    }
    
    void onCanceledDownload(const URL& url,
                            IByteBuffer* buffer,
                            bool expired) {
        // do nothing
    }
    
};

void G3MOSMBuildingsDemoScene::addMark(Mark* mark) {
    getModel()->getMarksRenderer()->addMark(mark);
}

void G3MOSMBuildingsDemoScene::rawActivate(const G3MContext* context) {
    
    //Used for downloader->requestBuffer call
    bool readExpired = true;
    bool deleteListener = true;
    
    G3MDemoModel* model     = getModel();
    G3MWidget*    g3mWidget = model->getG3MWidget();
    
    g3mWidget->setBackgroundColor(Color::fromRGBA(0.9f, 0.21f, 0.21f, 1.0f));
    
    BingMapsLayer* layer = new BingMapsLayer(BingMapType::Aerial(),
                                             "AnU5uta7s5ql_HTrRZcPLI4_zotvNefEeSxIClF1Jf7eS-mLig1jluUdCoecV7jc",
                                             TimeInterval::fromDays(30));
    model->getLayerSet()->addLayer(layer);
   
    IDownloader* downloader = context->getDownloader();
    
    _requestId = downloader->requestBuffer(URL("http://data.osmbuildings.org/0.2/rkc8ywdl/tile/14/4825/6156.json"),
                                           DownloadPriority::HIGHEST,
                                           TimeInterval::fromHours(1),
                                           readExpired,
                                           new G3MOSMBuildingsDemoScene_BufferDownloadListener(this),
                                           deleteListener);
    
    g3mWidget->setAnimatedCameraPosition(Geodetic3D::fromDegrees(13, 52, 3643920),
                                         Angle::zero(), // heading
                                         Angle::fromDegrees(30 - 90) // pitch
                                         );
}

void G3MOSMBuildingsDemoScene::deactivate(const G3MContext* context) {
    context->getDownloader()->cancelRequest(_requestId);
    
    G3MDemoScene::deactivate(context);
}

void G3MOSMBuildingsDemoScene::rawSelectOption(const std::string& option,
                                          int optionIndex) {
    
}
