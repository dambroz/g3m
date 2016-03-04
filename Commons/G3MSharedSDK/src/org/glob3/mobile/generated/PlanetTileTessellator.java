
  private Vector2I calculateResolution(PlanetRenderContext prc, Tile tile, Sector renderedSector)
  {
    Sector sector = tile._sector;
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#warning Chano_changed_behaviour: Cambio clave: resolución general sólo si no tenemos elevaciones en el tile.Si las tenemos, entonces la malla será acorde a esas elevaciones dadas, tengan el extent que tengan.
  
    MutableVector2I mutableResolution = prc._layerTilesRenderParameters._tileMeshResolution.asMutableVector2I();
  
      if (tile.getElevationData() != null)
      {
        mutableResolution = tile.getElevationData().getExtent().asMutableVector2I();
      }
  
    final Vector2I resolution = mutableResolution.asVector2I();
  
    final double latRatio = sector._deltaLatitude._degrees / renderedSector._deltaLatitude._degrees;
    final double lonRatio = sector._deltaLongitude._degrees / renderedSector._deltaLongitude._degrees;
  
    final IMathUtils mu = IMathUtils.instance();
  
    int resX = (int) mu.ceil((resolution._x / lonRatio));
    if (resX < 2)
    {
      resX = 2;
    }
  
    int resY = (int) mu.ceil((resolution._y / latRatio));
    if (resY < 2)
    {
      resY = 2;
    }
  
    final Vector2I meshRes = new Vector2I(resX, resY);
      //ILogger::instance()->logInfo("Calculated meshRes:"+meshRes.description());
    return meshRes;
  
  
    //  return rawResolution;
  
    //  /* testing for dynamic latitude-resolution */
    //  const double cos = sector._center._latitude.cosinus();
    //
    //  int resolutionY = (int) (rawResolution._y * cos);
    //  if (resolutionY < 8) {
    //    resolutionY = 8;
    //  }
    //
    //  int resolutionX = (int) (rawResolution._x * cos);
    //  if (resolutionX < 8) {
    //    resolutionX = 8;
    //  }
    //
    //  return Vector2I(resolutionX, resolutionY);
  }

package org.glob3.mobile.generated;

public class PlanetTileTessellator
         extends
            TileTessellator {
   private final boolean _skirted;
   private Sector        _renderedSector;


   private Vector2S calculateResolution(final PlanetRenderContext prc,
                                        final Tile tile,
                                        final Sector renderedSector) {
      final Sector sector = tile._sector;
      final Vector2S resolution = prc._layerTilesRenderParameters._tileMeshResolution;

      final double latRatio = sector._deltaLatitude._degrees / renderedSector._deltaLatitude._degrees;
      final double lonRatio = sector._deltaLongitude._degrees / renderedSector._deltaLongitude._degrees;

      final IMathUtils mu = IMathUtils.instance();

      short resX = (short) mu.ceil((resolution._x / lonRatio));
      if (resX < 2) {
         resX = 2;
      }

      short resY = (short) mu.ceil((resolution._y / latRatio));
      if (resY < 2) {
         resY = 2;
      }

      final Vector2S meshRes = new Vector2S(resX, resY);
      return meshRes;


      //  return rawResolution;

      //  /* testing for dynamic latitude-resolution */
      //  const double cos = sector._center._latitude.cosinus();
      //
      //  int resolutionY = (int) (rawResolution._y * cos);
      //  if (resolutionY < 8) {
      //    resolutionY = 8;
      //  }
      //
      //  int resolutionX = (int) (rawResolution._x * cos);
      //  if (resolutionX < 8) {
      //    resolutionX = 8;
      //  }
      //
      //  return Vector2I(resolutionX, resolutionY);
   }


   //C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
   //  Geodetic3D getGeodeticOnPlanetSurface(IMathUtils mu, Planet planet, ElevationData elevationData, float verticalExaggeration, Geodetic2D g);

   private boolean needsEastSkirt(final Sector tileSector) {
      if (_renderedSector == null) {
         return true;
      }
      return _renderedSector._upper._longitude.greaterThan(tileSector._upper._longitude);
   }


   private boolean needsNorthSkirt(final Sector tileSector) {
      if (_renderedSector == null) {
         return true;
      }
      return _renderedSector._upper._latitude.greaterThan(tileSector._upper._latitude);
   }


   private boolean needsWestSkirt(final Sector tileSector) {
      if (_renderedSector == null) {
         return true;
      }
      return _renderedSector._lower._longitude.lowerThan(tileSector._lower._longitude);
   }


   private boolean needsSouthSkirt(final Sector tileSector) {
      if (_renderedSector == null) {
         return true;
      }
      return _renderedSector._lower._latitude.lowerThan(tileSector._lower._latitude);
   }


   private Sector getRenderedSectorForTile(final Tile tile) {
      if (_renderedSector == null) {
         return tile._sector;
      }
      return tile._sector.intersection(_renderedSector);
   }


   private double createSurfaceVertices(final Vector2S meshResolution,
                                        final Sector meshSector,
                                        final ElevationData elevationData,
                                        final float verticalExaggeration,
                                        final FloatBufferBuilderFromGeodetic vertices,
                                        final TileTessellatorMeshData data) {

      final IMathUtils mu = IMathUtils.instance();
      double minElevation = mu.maxDouble();
      double maxElevation = mu.minDouble();
      double averageElevation = 0;

      for (int j = 0; j < meshResolution._y; j++) {
         final double v = (double) j / (meshResolution._y - 1);

         for (int i = 0; i < meshResolution._x; i++) {
            final double u = (double) i / (meshResolution._x - 1);
            final Geodetic2D position = meshSector.getInnerPoint(u, v);
            double elevation = 0;

            if (elevationData != null) {
               final double rawElevation = elevationData.getElevationAt(position);

               elevation = (rawElevation != rawElevation) ? 0 : rawElevation * verticalExaggeration;

               //MIN
               if (elevation < minElevation) {
                  minElevation = elevation;
               }

               //MAX
               if (elevation > maxElevation) {
                  maxElevation = elevation;
               }

               //AVERAGE
               averageElevation += elevation;
            }

            vertices.add(position, elevation);
         }
      }

      if (minElevation == mu.maxDouble()) {
         minElevation = 0;
      }
      if (maxElevation == mu.minDouble()) {
         maxElevation = 0;
      }

      data._minHeight = minElevation;
      data._maxHeight = maxElevation;
      data._averageHeight = averageElevation / (meshResolution._x * meshResolution._y);

      return minElevation;
   }


   private double createSurface(final Sector tileSector,
                                final Sector meshSector,
                                final Vector2S meshResolution,
                                final ElevationData elevationData,
                                final float verticalExaggeration,
                                final boolean mercator,
                                final FloatBufferBuilderFromGeodetic vertices,
                                final ShortBufferBuilder indices,
                                final FloatBufferBuilderFromCartesian2D textCoords,
                                final TileTessellatorMeshData data) {

      //VERTICES///////////////////////////////////////////////////////////////
      final double minElevation = createSurfaceVertices(new Vector2S(meshResolution._x, meshResolution._y), meshSector,
               elevationData, verticalExaggeration, vertices, data);


      //TEX COORDINATES////////////////////////////////////////////////////////////////

      if (mercator) //Mercator
      {

         final double mercatorLowerGlobalV = MercatorUtils.getMercatorV(tileSector._lower._latitude);
         final double mercatorUpperGlobalV = MercatorUtils.getMercatorV(tileSector._upper._latitude);
         final double mercatorDeltaGlobalV = mercatorLowerGlobalV - mercatorUpperGlobalV;

         for (int j = 0; j < meshResolution._y; j++) {
            final double v = (double) j / (meshResolution._y - 1);

            for (int i = 0; i < meshResolution._x; i++) {
               final double u = (double) i / (meshResolution._x - 1);

               final Angle lat = Angle.linearInterpolation(meshSector._lower._latitude, meshSector._upper._latitude, 1.0 - v);
               final Angle lon = Angle.linearInterpolation(meshSector._lower._longitude, meshSector._upper._longitude, u);

               //U
               final double m_u = tileSector.getUCoordinate(lon);

               //V
               final double mercatorGlobalV = MercatorUtils.getMercatorV(lat);
               final double m_v = (mercatorGlobalV - mercatorUpperGlobalV) / mercatorDeltaGlobalV;

               textCoords.add((float) m_u, (float) m_v);
            }
         }

      } //No mercator
      else {

         for (int j = 0; j < meshResolution._y; j++) {
            final double v = (double) j / (meshResolution._y - 1);
            for (int i = 0; i < meshResolution._x; i++) {
               final double u = (double) i / (meshResolution._x - 1);
               textCoords.add((float) u, (float) v);
            }
         }

      }

      //INDEX///////////////////////////////////////////////////////////////
      for (short j = 0; j < (meshResolution._y - 1); j++) {
         final short jTimesResolution = (short) (j * meshResolution._x);
         if (j > 0) {
            indices.add(jTimesResolution);
         }
         for (short i = 0; i < meshResolution._x; i++) {
            indices.add((short) (jTimesResolution + i));
            indices.add((short) (jTimesResolution + i + meshResolution._x));
         }
         indices.add((short) ((jTimesResolution + (2 * meshResolution._x)) - 1));
      }

      return minElevation;
   }


   private void createEastSkirt(final Planet planet,
                                final Sector tileSector,
                                final Sector meshSector,
                                final Vector2S meshResolution,
                                final double skirtHeight,
                                final FloatBufferBuilderFromGeodetic vertices,
                                final ShortBufferBuilder indices,
                                final FloatBufferBuilderFromCartesian2D textCoords) {

      //VERTICES///////////////////////////////////////////////////////////////
      final short firstSkirtVertex = (short) (vertices.size() / 3);

      final short southEastCorner = (short) ((meshResolution._x * meshResolution._y) - 1);

      short skirtIndex = firstSkirtVertex;
      short surfaceIndex = southEastCorner;

      // east side
      for (short j = (short) (meshResolution._y - 1); j >= 0; j--) {
         final double x = 1;
         final double y = (double) j / (meshResolution._y - 1);
         final Geodetic2D g = meshSector.getInnerPoint(x, y);
         vertices.add(g, skirtHeight);

         //TEXTURE COORDS/////////////////////////////
         final Vector2D uv = textCoords.getVector2D(surfaceIndex);
         textCoords.add(uv);

         //INDEX///////////////////////////////////////////////////////////////
         indices.add(surfaceIndex);
         indices.add(skirtIndex);

         skirtIndex++;
         surfaceIndex -= meshResolution._x;
      }
      //Short casts are needed due to widening primitive conversions in java
      //http://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.6.2
      indices.add((short) (surfaceIndex + meshResolution._x));
      indices.add((short) (surfaceIndex + meshResolution._x));
   }


   private void createNorthSkirt(final Planet planet,
                                 final Sector tileSector,
                                 final Sector meshSector,
                                 final Vector2S meshResolution,
                                 final double skirtHeight,
                                 final FloatBufferBuilderFromGeodetic vertices,
                                 final ShortBufferBuilder indices,
                                 final FloatBufferBuilderFromCartesian2D textCoords) {

      //VERTICES///////////////////////////////////////////////////////////////
      final short firstSkirtVertex = (short) (vertices.size() / 3);
      final short northEastCorner = (short) (meshResolution._x - 1);

      short skirtIndex = firstSkirtVertex;
      short surfaceIndex = northEastCorner;

      indices.add(surfaceIndex);

      for (short i = (short) (meshResolution._x - 1); i >= 0; i--) {
         final double x = (double) i / (meshResolution._x - 1);
         final double y = 0;
         final Geodetic2D g = meshSector.getInnerPoint(x, y);
         vertices.add(g, skirtHeight);

         //TEXTURE COORDS/////////////////////////////
         final Vector2D uv = textCoords.getVector2D(surfaceIndex);
         textCoords.add(uv);

         //INDEX///////////////////////////////////////////////////////////////
         indices.add(surfaceIndex);
         indices.add(skirtIndex);

         skirtIndex++;
         surfaceIndex -= 1;
      }

      indices.add((short) (surfaceIndex + 1));
      indices.add((short) (surfaceIndex + 1));
   }


   private void createWestSkirt(final Planet planet,
                                final Sector tileSector,
                                final Sector meshSector,
                                final Vector2S meshResolution,
                                final double skirtHeight,
                                final FloatBufferBuilderFromGeodetic vertices,
                                final ShortBufferBuilder indices,
                                final FloatBufferBuilderFromCartesian2D textCoords) {

      //VERTICES///////////////////////////////////////////////////////////////
      final short firstSkirtVertex = (short) (vertices.size() / 3);

      final short northWestCorner = (short) 0;

      short skirtIndex = firstSkirtVertex;
      short surfaceIndex = northWestCorner;

      indices.add(surfaceIndex);

      for (short j = 0; j < meshResolution._y; j++) {
         final double x = 0;
         final double y = (double) j / (meshResolution._y - 1);
         final Geodetic2D g = meshSector.getInnerPoint(x, y);
         vertices.add(g, skirtHeight);

         //TEXTURE COORDS/////////////////////////////
         final Vector2D uv = textCoords.getVector2D(surfaceIndex);
         textCoords.add(uv);

         //INDEX///////////////////////////////////////////////////////////////
         indices.add(surfaceIndex);
         indices.add(skirtIndex);

         skirtIndex++;
         surfaceIndex += meshResolution._x;
      }

      indices.add((short) (surfaceIndex - meshResolution._x));
      indices.add((short) (surfaceIndex - meshResolution._x));
   }


   private void createSouthSkirt(final Planet planet,
                                 final Sector tileSector,
                                 final Sector meshSector,
                                 final Vector2S meshResolution,
                                 final double skirtHeight,
                                 final FloatBufferBuilderFromGeodetic vertices,
                                 final ShortBufferBuilder indices,
                                 final FloatBufferBuilderFromCartesian2D textCoords) {

      //VERTICES///////////////////////////////////////////////////////////////
      final short firstSkirtVertex = (short) (vertices.size() / 3);

      final short southWestCorner = (short) (meshResolution._x * (meshResolution._y - 1));

      short skirtIndex = firstSkirtVertex;
      short surfaceIndex = southWestCorner;

      indices.add(surfaceIndex);

      for (short i = 0; i < meshResolution._x; i++) {
         final double x = (double) i / (meshResolution._x - 1);
         final double y = 1;
         final Geodetic2D g = meshSector.getInnerPoint(x, y);
         vertices.add(g, skirtHeight);

         //TEXTURE COORDS/////////////////////////////
         final Vector2D uv = textCoords.getVector2D(surfaceIndex);
         textCoords.add((float) uv._x, (float) uv._y);

         //INDEX///////////////////////////////////////////////////////////////
         indices.add(surfaceIndex);
         indices.add(skirtIndex++);
         surfaceIndex += 1;
      }

  public final Mesh createTileDebugMesh(G3MRenderContext rc, PlanetRenderContext prc, Tile tile)
  {
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#warning TODO for JM!
    return null;
  
    /*const Sector meshSector = getRenderedSectorForTile(tile);
    const Vector2I meshResolution = calculateResolution(prc, tile, meshSector);
    const short rx = (short)meshResolution._x;
    const short ry = (short)meshResolution._y;
    
    AbstractGeometryMesh* mesh = ((AbstractGeometryMesh*)tile->getTessellatorMesh());
    const IFloatBuffer* vertices = mesh->getVertices();
  
    //INDEX OF BORDER///////////////////////////////////////////////////////////////
    ShortBufferBuilder indicesBorder;
    for (short j = 0; j < rx; j++) {
      indicesBorder.add(j);
    }
    
    for (short i = 2; i < ry+1; i++) {
      indicesBorder.add((short)((i * rx)-1));
    }
    
    for (short j = (short)(rx*ry-2); j >= (short)(rx*(ry-1)); j--) {
      indicesBorder.add(j);
    }
    
    for (short j = (short)(rx*(ry-1)-rx); j >= 0; j-=rx) {
      indicesBorder.add(j);
    }
    
    //INDEX OF GRID
    ShortBufferBuilder indicesGrid;
    for (short i = 0; i < ry-1; i++) {
      short rowOffset = (short)(i * rx);
    
      for (short j = 0; j < rx; j++) {
        indicesGrid.add((short)(rowOffset + j));
        indicesGrid.add((short)(rowOffset + j+rx));
      }
      for (short j = (short)((2*rx)-1); j >= rx; j--) {
        indicesGrid.add((short)(rowOffset + j));
      }
    
    }
    
    const Color levelColor = Color::blue().wheelStep(5, tile->_level % 5);
    const float gridLineWidth = tile->isElevationDataSolved() || (tile->getElevationData() == NULL) ? 1.0f : 3.0f;
    
    
    IndexedMesh* border = new IndexedMesh(GLPrimitive::lineStrip(),
                                          mesh->getCenter(),
                                          (IFloatBuffer*)vertices,
                                          false,
                                          indicesBorder.create(),
                                          true,
                                          2.0f,
                                          1.0f,
                                          Color::newFromRGBA(1.0f, 0.0f, 0.0f, 1.0f),
                                          NULL,
                                          1.0f,
                                          false,
                                          NULL,
                                          true, 1.0f, 1.0f);
    
    IndexedMesh* grid = new IndexedMesh(GLPrimitive::lineStrip(),
                                        mesh->getCenter(),
                                        (IFloatBuffer*)vertices,
                                        false,
                                        indicesGrid.create(),
                                        true,
                                        gridLineWidth,
                                        1.0f,
                                        new Color(levelColor),
                                        NULL,
                                        1.0f,
                                        false,
                                        NULL,
                                        true, 1.0f, 1.0f);
    
    CompositeMesh* c = new CompositeMesh();
    c->addMesh(grid);
    c->addMesh(border);
    
    return c;*/
  }


   private static double skirtDepthForSector(final Planet planet,
                                             final Sector sector) {

      final Vector3D se = planet.toCartesian(sector.getSE());
      final Vector3D nw = planet.toCartesian(sector.getNW());
      final double diagonalLength = nw.sub(se).length();
      final double sideLength = diagonalLength * 0.70710678118;
      //0.707 = 1 / SQRT(2) -> diagonalLength => estimated side length
      return sideLength / 20.0;
   }


   public PlanetTileTessellator(final boolean skirted,
                                final Sector sector) {
      _skirted = skirted;
      _renderedSector = sector.isEquals(Sector.fullSphere()) ? null : new Sector(sector);
   }


   @Override
   public void dispose() {
      _renderedSector = null;
      super.dispose();

   }


   @Override
   public final Vector2S getTileMeshResolution(final G3MRenderContext rc,
                                               final PlanetRenderContext prc,
                                               final Tile tile) {
      final Sector sector = getRenderedSectorForTile(tile);
      return calculateResolution(prc, tile, sector);
   }


   @Override
   public final Mesh createTileMesh(final G3MRenderContext rc,
                                    final PlanetRenderContext prc,
                                    final Tile tile,
                                    final ElevationData elevationData,
                                    final TileTessellatorMeshData data) {

      final Sector tileSector = tile._sector;
      final Sector meshSector = getRenderedSectorForTile(tile);
      final Vector2S meshResolution = calculateResolution(prc, tile, meshSector);

      final Planet planet = rc.getPlanet();

      final FloatBufferBuilderFromGeodetic vertices = FloatBufferBuilderFromGeodetic.builderWithGivenCenter(planet,
               meshSector._center);
      final ShortBufferBuilder indices = new ShortBufferBuilder();
      final FloatBufferBuilderFromCartesian2D textCoords = new FloatBufferBuilderFromCartesian2D();

      final double minElevation = createSurface(tileSector, meshSector, meshResolution, elevationData, prc._verticalExaggeration,
               tile._mercator, vertices, indices, textCoords, data);

      if (_skirted) {
         final double relativeSkirtHeight = minElevation - skirtDepthForSector(planet, tileSector);

         double absoluteSkirtHeight = 0;
         if (_renderedSector != null) {
            absoluteSkirtHeight = -skirtDepthForSector(planet, _renderedSector);
         }

         createEastSkirt(planet, tileSector, meshSector, meshResolution, needsEastSkirt(tileSector) ? relativeSkirtHeight
                                                                                                   : absoluteSkirtHeight,
                  vertices, indices, textCoords);

         createNorthSkirt(planet, tileSector, meshSector, meshResolution, needsNorthSkirt(tileSector) ? relativeSkirtHeight
                                                                                                     : absoluteSkirtHeight,
                  vertices, indices, textCoords);

         createWestSkirt(planet, tileSector, meshSector, meshResolution, needsWestSkirt(tileSector) ? relativeSkirtHeight
                                                                                                   : absoluteSkirtHeight,
                  vertices, indices, textCoords);

         createSouthSkirt(planet, tileSector, meshSector, meshResolution, needsSouthSkirt(tileSector) ? relativeSkirtHeight
                                                                                                     : absoluteSkirtHeight,
                  vertices, indices, textCoords);
      }

      //Storing textCoords in Tile
      tile.setTessellatorData(new PlanetTileTessellatorData(textCoords));

      final IFloatBuffer verticesB = vertices.create();
      final IShortBuffer indicesB = indices.create();
      final IFloatBuffer normals = null;
      ///#warning Testing_Terrain_Normals;
      //  IFloatBuffer* normals = NormalsUtils::createTriangleStripSmoothNormals(verticesB, indicesB);

      final Mesh result = new IndexedGeometryMesh(GLPrimitive.triangleStrip(), vertices.getCenter(), verticesB, true, normals,
               true, indicesB, true);

      if (vertices != null) {
         vertices.dispose();
      }

      return result;
   }


   @Override
   public final Mesh createTileDebugMesh(final G3MRenderContext rc,
                                         final PlanetRenderContext prc,
                                         final Tile tile) {
      final Sector meshSector = getRenderedSectorForTile(tile);
      final Vector2S meshResolution = calculateResolution(prc, tile, meshSector);

      final FloatBufferBuilderFromGeodetic vertices = FloatBufferBuilderFromGeodetic.builderWithFirstVertexAsCenter(rc.getPlanet());
      final TileTessellatorMeshData data = new TileTessellatorMeshData();
      createSurfaceVertices(meshResolution, meshSector, tile.getElevationData(), prc._verticalExaggeration, vertices, data);

      //INDEX OF BORDER///////////////////////////////////////////////////////////////
      final ShortBufferBuilder indicesBorder = new ShortBufferBuilder();
      for (short j = 0; j < meshResolution._x; j++) {
         indicesBorder.add(j);
      }

      for (short i = 2; i < (meshResolution._y + 1); i++) {
         indicesBorder.add((short) ((i * meshResolution._x) - 1));
      }

      for (short j = (short) ((meshResolution._x * meshResolution._y) - 2); j >= (meshResolution._x * (meshResolution._y - 1)); j--) {
         indicesBorder.add(j);
      }

      for (short j = (short) ((meshResolution._x * (meshResolution._y - 1)) - meshResolution._x); j >= 0; j -= meshResolution._x) {
         indicesBorder.add(j);
      }

      //INDEX OF GRID
      final ShortBufferBuilder indicesGrid = new ShortBufferBuilder();
      for (short i = 0; i < (meshResolution._y - 1); i++) {
         final short rowOffset = (short) (i * meshResolution._x);

         for (short j = 0; j < meshResolution._x; j++) {
            indicesGrid.add((short) (rowOffset + j));
            indicesGrid.add((short) (rowOffset + j + meshResolution._x));
         }
         for (short j = (short) ((2 * meshResolution._x) - 1); j >= meshResolution._x; j--) {
            indicesGrid.add((short) (rowOffset + j));
         }

      }

      final Color levelColor = Color.blue().wheelStep(5, tile._level % 5);
      final float gridLineWidth = tile.isElevationDataSolved() || (tile.getElevationData() == null) ? 1.0f : 3.0f;


      final IndexedMesh border = new IndexedMesh(GLPrimitive.lineStrip(), vertices.getCenter(), vertices.create(), true,
               indicesBorder.create(), true, 2.0f, 1.0f, Color.newFromRGBA(1.0f, 0.0f, 0.0f, 1.0f), null, 1.0f, false, null,
               true, 1.0f, 1.0f);

      final IndexedMesh grid = new IndexedMesh(GLPrimitive.lineStrip(), vertices.getCenter(), vertices.create(), true,
               indicesGrid.create(), true, gridLineWidth, 1.0f, new Color(levelColor), null, 1.0f, false, null, true, 1.0f, 1.0f);

      if (vertices != null) {
         vertices.dispose();
      }

      final CompositeMesh c = new CompositeMesh();
      c.addMesh(grid);
      c.addMesh(border);

      return c;
   }


   @Override
   public final IFloatBuffer createTextCoords(final Vector2S rawResolution,
                                              final Tile tile) {

      final PlanetTileTessellatorData data = tile.getTessellatorData();
      if ((data == null) || (data._textCoords == null)) {
         ILogger.instance().logError("Logic error on PlanetTileTessellator::createTextCoord");
         return null;
      }
      return data._textCoords.create();
   }


   @Override
   public final Vector2F getTextCoord(final Tile tile,
                                      final Angle latitude,
                                      final Angle longitude) {
      final Sector sector = tile._sector;

      final Vector2F linearUV = sector.getUVCoordinatesF(latitude, longitude);
      if (!tile._mercator) {
         return linearUV;
      }

      final double lowerGlobalV = MercatorUtils.getMercatorV(sector._lower._latitude);
      final double upperGlobalV = MercatorUtils.getMercatorV(sector._upper._latitude);
      final double deltaGlobalV = lowerGlobalV - upperGlobalV;

      final double globalV = MercatorUtils.getMercatorV(latitude);
      final double localV = (globalV - upperGlobalV) / deltaGlobalV;

      return new Vector2F(linearUV._x, (float) localV);
   }


   @Override
   public final void setRenderedSector(final Sector sector) {
      if ((_renderedSector == null) || !_renderedSector.isEquals(sector)) {
         _renderedSector = null;

         if (sector.isEquals(Sector.fullSphere())) {
            _renderedSector = null;
         }
         else {
            _renderedSector = new Sector(sector);
         }
      }
   }

}
