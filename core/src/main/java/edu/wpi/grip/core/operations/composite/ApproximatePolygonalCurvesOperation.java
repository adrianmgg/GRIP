package edu.wpi.grip.core.operations.composite;

import edu.wpi.grip.annotation.operation.Description;
import edu.wpi.grip.annotation.operation.OperationCategory;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import edu.wpi.grip.core.sockets.SocketHints;

import java.util.List;

import static org.bytedeco.javacpp.opencv_core.MatVector;
import static org.bytedeco.javacpp.opencv_imgproc.approxPolyDP;

// FIXME needs code generation for cpp, python, java
/**
 * An {@link Operation} that TODO
 */
@Description(name = "Approximate Polygonal Curves",
		summary = "Summary TODO", // TODO
		category = OperationCategory.FEATURE_DETECTION)
public class ApproximatePolygonalCurvesOperation implements Operation {

	private final SocketHint<ContoursReport> contoursHint = new SocketHint.Builder<>(ContoursReport
			.class)
			.identifier("Contours").initialValueSupplier(ContoursReport::new).build();
	// TODO not sure what a good default / low / high for epsilon would be
	// TODO make it more obvious to user wtf epsilon is ("Parameter specifying the approximation accuracy. This is the maximum distance between the original curve and its approximation.")
	private final SocketHint<Number> epsilonHint = SocketHints.Inputs.createNumberSliderSocketHint("Epsilon", 5d, 0d, 32d);
	private final SocketHint<Boolean> closedHint = SocketHints.Inputs.createCheckboxSocketHint("Closed", true);

	private final InputSocket<ContoursReport> inputSocket;
	private final InputSocket<Number> epsilonSocket;
	private final InputSocket<Boolean> closedSocket;
	private final OutputSocket<ContoursReport> outputSocket;

	@Inject
	@SuppressWarnings("JavadocMethod")
	public ApproximatePolygonalCurvesOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory
			outputSocketFactory) {
		this.inputSocket = inputSocketFactory.create(contoursHint);
		this.epsilonSocket = inputSocketFactory.create(epsilonHint);
		this.closedSocket = inputSocketFactory.create(closedHint);
		this.outputSocket = outputSocketFactory.create(contoursHint);
	}

	@Override
	public List<InputSocket> getInputSockets() {
		return ImmutableList.of(
				inputSocket,
				epsilonSocket,
				closedSocket
		);
	}

	@Override
	public List<OutputSocket> getOutputSockets() {
		return ImmutableList.of(
				outputSocket
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void perform() {
		final MatVector inputContours = inputSocket.getValue().get().getContours();
		final double epsilon = epsilonSocket.getValue().get().doubleValue();
		final boolean closed = closedSocket.getValue().get();
		final MatVector outputContours = new MatVector(inputContours.size());

		for (int i = 0; i < inputContours.size(); i++) {
			approxPolyDP(inputContours.get(i), outputContours.get(i), epsilon, closed);
		}

		outputSocket.setValue(new ContoursReport(outputContours,
				inputSocket.getValue().get().getRows(), inputSocket.getValue().get().getCols()));
	}
}
