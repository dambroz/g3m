//
//  Shape.hpp
//  G3MiOSSDK
//
//  Created by Diego Gomez Deck on 10/28/12.
//
//

#ifndef __G3MiOSSDK__Shape__
#define __G3MiOSSDK__Shape__

#include "Geodetic3D.hpp"
#include "Context.hpp"
#include "Vector3D.hpp"

class MutableMatrix44D;
class G3MRenderContext;
class BoundingVolume;


#include "Effects.hpp"
#include <vector>

#include "GLState.hpp"

#include "SurfaceElevationProvider.hpp"

#include "Geodetic3D.hpp"

class ShapePendingEffect;
class GPUProgramState;

class Shape : public SurfaceElevationListener, EffectTarget {
private:
  Geodetic3D* _position;
  AltitudeMode _altitudeMode;
  
  Angle*      _heading;
  Angle*      _pitch;
  Angle*      _roll;
  
  double      _scaleX;
  double      _scaleY;
  double      _scaleZ;

  double      _translationX;
  double      _translationY;
  double      _translationZ;

//  const Planet* _planet;

  mutable MutableMatrix44D* _transformMatrix;
  
  std::vector<ShapePendingEffect*> _pendingEffects;

  bool _enable;
  
  mutable GLState* _glState;

  SurfaceElevationProvider* _surfaceElevationProvider;
  double _surfaceElevation;
  
  MutableMatrix44D* createTransformMatrix(const Planet* planet) const;
  
  bool _selected;

  
protected:
  virtual void cleanTransformMatrix();
  MutableMatrix44D* getTransformMatrix(const Planet* planet) const;

  virtual BoundingVolume* getBoundingVolume(const G3MRenderContext *rc) = 0;
  
  
public:
  
  Shape(Geodetic3D* position,
        AltitudeMode altitudeMode) :
  _position( position ),
  _altitudeMode(altitudeMode),
  _heading( new Angle(Angle::zero()) ),
  _pitch( new Angle(Angle::zero()) ),
  _roll( new Angle(Angle::zero()) ),
  _scaleX(1),
  _scaleY(1),
  _scaleZ(1),
  _translationX(0),
  _translationY(0),
  _translationZ(0),
  _transformMatrix(NULL),
  _enable(true),
  _surfaceElevation(0),
  _glState(new GLState()),
  _selected(false),
  _surfaceElevationProvider(NULL)
  {
    
  }
  
  virtual ~Shape();
  
  const Geodetic3D getPosition() const {
    return *_position;
  }
  
  const Angle getHeading() const {
    return *_heading;
  }
  
  const Angle getPitch() const {
    return *_pitch;
  }

  const Angle getRoll() const {
    return *_roll;
  }

  void setPosition(Geodetic3D* position,
                   AltitudeMode altitudeMode) {
    delete _position;
    _position = position;
    _altitudeMode = altitudeMode;
    cleanTransformMatrix();
  }

  void setPosition(Geodetic3D* position) {
    delete _position;
    _position = position;
    cleanTransformMatrix();
  }

  void addShapeEffect(Effect* effect);
  
  void setAnimatedPosition(const TimeInterval& duration,
                           const Geodetic3D& position,
                           bool linearInterpolation=false);
  
  void setAnimatedPosition(const TimeInterval& duration,
                           const Geodetic3D& position,
                           const Angle& pitch,
                           const Angle& heading,
                           const Angle& roll,
                           bool linearInterpolation=false);
  
  void setAnimatedPosition(const Geodetic3D& position,
                           bool linearInterpolation=false) {
    setAnimatedPosition(TimeInterval::fromSeconds(3),
                        position,
                        linearInterpolation);
  }
  
  void setHeading(const Angle& heading) {
#ifdef C_CODE
    delete _heading;
    _heading = new Angle(heading);
#endif
#ifdef JAVA_CODE
    _heading = heading;
#endif
    cleanTransformMatrix();
  }
  
  void setPitch(const Angle& pitch) {
#ifdef C_CODE
    delete _pitch;
    _pitch = new Angle(pitch);
#endif
#ifdef JAVA_CODE
    _pitch = pitch;
#endif
    cleanTransformMatrix();
  }

  void setRoll(const Angle& roll) {
#ifdef C_CODE
    delete _roll;
    _roll = new Angle(roll);
#endif
#ifdef JAVA_CODE
    _roll = roll;
#endif
    cleanTransformMatrix();
  }
  
  void setScale(double scale) {
    setScale(scale, scale, scale);
  }

  void setTranslation(const Vector3D& translation) {
    setTranslation(translation._x,
                   translation._y,
                   translation._z);
  }

  void setTranslation(double translationX,
                      double translationY,
                      double translationZ) {
    _translationX = translationX;
    _translationY = translationY;
    _translationZ = translationZ;
    cleanTransformMatrix();
  }
  
  void setScale(double scaleX,
                double scaleY,
                double scaleZ) {
    _scaleX = scaleX;
    _scaleY = scaleY;
    _scaleZ = scaleZ;
    cleanTransformMatrix();
  }
  
  void setScale(const Vector3D& scale) {
    setScale(scale._x,
             scale._y,
             scale._z);
  }
  
  Vector3D getScale() const {
    return Vector3D(_scaleX,
                    _scaleY,
                    _scaleZ);
  }
  
  void setAnimatedScale(const TimeInterval& duration,
                        double scaleX,
                        double scaleY,
                        double scaleZ);
  
  void setAnimatedScale(double scaleX,
                        double scaleY,
                        double scaleZ) {
    setAnimatedScale(TimeInterval::fromSeconds(1),
                     scaleX,
                     scaleY,
                     scaleZ);
  }
  
  void setAnimatedScale(const Vector3D& scale) {
    setAnimatedScale(scale._x,
                     scale._y,
                     scale._z);
  }
  
  void setAnimatedScale(const TimeInterval& duration,
                        const Vector3D& scale) {
    setAnimatedScale(duration,
                     scale._x,
                     scale._y,
                     scale._z);
  }
  
  void orbitCamera(const TimeInterval& duration,
                   double fromDistance,       double toDistance,
                   const Angle& fromAzimuth,  const Angle& toAzimuth,
                   const Angle& fromAltitude, const Angle& toAltitude);

  bool isEnable() const {
    return _enable;
  }

  void setEnable(bool enable) {
    _enable = enable;
  }

  void render(const G3MRenderContext* rc,
              GLState* parentState,
              bool renderNotReadyShapes);

  virtual void initialize(const G3MContext* context) {
    if (_altitudeMode == RELATIVE_TO_GROUND) {
      _surfaceElevationProvider = context->getSurfaceElevationProvider();
      if (_surfaceElevationProvider != NULL) {
        _surfaceElevationProvider->addListener(_position->_latitude,
                                               _position->_longitude,
                                               this);
      }
    }
  }

  virtual bool isReadyToRender(const G3MRenderContext* rc) = 0;

  virtual void rawRender(const G3MRenderContext* rc,
                         GLState* parentGLState,
                         bool renderNotReadyShapes) = 0;

  virtual bool isTransparent(const G3MRenderContext* rc) = 0;

  void elevationChanged(const Geodetic2D& position,
                        double rawElevation,            // Without considering vertical exaggeration
                        double verticalExaggeration);

  void elevationChanged(const Sector& position,
                   const ElevationData* rawElevationData, // Without considering vertical exaggeration
                        double verticalExaggeration) {}
  
  virtual std::vector<double> intersectionsDistances(const Planet* planet,
                                                     const Camera* camera,
                                                     const Vector3D& origin,
                                                     const Vector3D& direction) = 0;
  
  virtual bool isVisible(const G3MRenderContext *rc) = 0;
  
  virtual void setSelectedDrawMode(bool mode) = 0;
  
  
  void select() {
    _selected = true;
    setSelectedDrawMode(true);
  }
  
  void unselect() {
    _selected = false;
    setSelectedDrawMode(false);
  }

  bool isSelected() { return _selected; }
  
  virtual GEORasterSymbol* createRasterSymbolIfNeeded() const = 0;
  
  virtual std::vector<Geodetic2D*> getCopyRasterCoordinates() const {
    std::vector<Geodetic2D*> emptyList;
    return emptyList;
  }
  
  virtual bool isRaster() const {
    return false;
  }

  void zRender(const G3MRenderContext* rc, GLState* parentState, bool renderNotReadyShapes);

  virtual void zRawRender(const G3MRenderContext* rc, GLState* parentGLState) = 0;

  virtual double getLength() const {
    return 0;
  }
  
  virtual double getArea() const {
    return 0;
  }
};

#endif
