

package com.glob3mobile.vectorial.cluster.mapdb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.mapdb.Serializer;

import com.glob3mobile.geo.Geodetic2D;
import com.glob3mobile.geo.Sector;
import com.glob3mobile.vectorial.cluster.nodes.CInnerNodeHeader;
import com.glob3mobile.vectorial.cluster.nodes.CLeafNodeHeader;
import com.glob3mobile.vectorial.cluster.nodes.CNodeHeader;
import com.glob3mobile.vectorial.storage.mapdb.SerializerUtils;


public class CNodeHeaderSerializer
   implements
      Serializer<CNodeHeader>,
      Serializable {


   private static final long serialVersionUID = 1L;


   @Override
   public void serialize(final DataOutput out,
                         final CNodeHeader node) throws IOException {
      if (node instanceof CLeafNodeHeader) {
         out.writeBoolean(true);
         serialize(out, (CLeafNodeHeader) node);
      }
      else if (node instanceof CInnerNodeHeader) {
         out.writeBoolean(true);
         serialize(out, (CInnerNodeHeader) node);
      }
      else {
         throw new RuntimeException("Unknown node type: " + node.getClass());
      }
   }


   private void serialize(final DataOutput out,
                          final CLeafNodeHeader node) throws IOException {
      SerializerUtils.serialize(out, node.getNodeSector());
      SerializerUtils.serialize(out, node.getMinimumSector());
      SerializerUtils.serialize(out, node.getAveragePosition());
      out.writeInt(node.getFeaturesCount());
   }


   @Override
   public CNodeHeader deserialize(final DataInput in,
                                  final int available) throws IOException {
      final boolean isLeaf = in.readBoolean();
      return isLeaf ? deserializeLeaf(in) : deserializeInner(in);
   }


   private CLeafNodeHeader deserializeLeaf(final DataInput in) throws IOException {
      final Sector nodeSector = SerializerUtils.deserializeSector(in);
      final Sector minimumSector = SerializerUtils.deserializeSector(in);
      final Geodetic2D averagePosition = SerializerUtils.deserializeGeodetic2D(in);
      final int featuresCount = in.readInt();
      return new CLeafNodeHeader(nodeSector, minimumSector, averagePosition, featuresCount);
   }


   private void serialize(final DataOutput out,
                          final CInnerNodeHeader node) throws IOException {
      SerializerUtils.serialize(out, node.getNodeSector());
      SerializerUtils.serialize(out, node.getMinimumSector());
      SerializerUtils.serialize(out, node.getAveragePosition());
      out.writeInt(node.getClustersCount());
   }


   private CInnerNodeHeader deserializeInner(final DataInput in) throws IOException {
      final Sector nodeSector = SerializerUtils.deserializeSector(in);
      final Sector minimumSector = SerializerUtils.deserializeSector(in);
      final Geodetic2D averagePosition = SerializerUtils.deserializeGeodetic2D(in);
      final int clustersCount = in.readInt();
      return new CInnerNodeHeader(nodeSector, minimumSector, averagePosition, clustersCount);
   }


   @Override
   public int fixedSize() {
      return -1;
   }


}
