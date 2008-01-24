package playground.david.vis.handler;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

import org.matsim.mobsim.QueueLink;
import org.matsim.mobsim.snapshots.PositionInfo;
import org.matsim.mobsim.snapshots.PositionInfo.VehicleState;

import playground.david.vis.data.OTFDataWriter;
import playground.david.vis.data.OTFWriterFactory;

public class OTFLinkAgentsNoParkingHandler extends OTFLinkAgentsHandler {
	static public class Writer extends  OTFLinkAgentsHandler.Writer implements Serializable, OTFWriterFactory<QueueLink>{
		@Override
		public void writeDynData(ByteBuffer out) throws IOException {
			// do not call this! IT writes all agents super.writeDynData(out);
			// Write additional agent data
	        /*
	         * (4) write agents
	         */
	        positions.clear();
			src.getVehiclePositions(positions);
			int valid = 0;
			for (PositionInfo pos : positions) {
				if (pos.getVehicleState() != VehicleState.Parking) valid++;
			}
			out.putInt(valid);

			for (PositionInfo pos : positions) {
				if (pos.getVehicleState() != VehicleState.Parking) writeAgent(pos, out);
			}
		}

		@Override
		public OTFDataWriter<QueueLink> getWriter() {
			return new Writer();
		}

	}
	
}
