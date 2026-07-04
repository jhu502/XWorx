package plm.three;

import javax.vecmath.Matrix3d;

public class RotationMatrix {
	private Matrix3d matrix3d;

	public RotationMatrix(Matrix3d matrix) {
		matrix3d = matrix;
	}

	public EulerAngles toEulerAngles() {
		double R00 = matrix3d.getM00(), R01 = matrix3d.getM01(), R02 = matrix3d.getM02();
		double R10 = matrix3d.getM10(), R11 = matrix3d.getM11(), R12 = matrix3d.getM12();
		double R20 = matrix3d.getM20(), R21 = matrix3d.getM21(), R22 = matrix3d.getM22();

		double SY = Math.sqrt(R00 * R00 + R10 * R10);

		boolean singular = SY < 1e-6;

		double x, y, z;
		if (!singular) {
			x = Math.atan2(R21, R22);
			y = Math.atan2(-R20, SY);
			z = Math.atan2(R10, R00);
		} else {
			x = Math.atan2(-R12, R11);
			y = Math.atan2(-R20, SY);
			z = 0;
		}
		x = x * 180.0 / Math.PI;
		y = y * 180.0 / Math.PI;
		z = z * 180.0 / Math.PI;
		
		return new EulerAngles(x, y, z);
	}
}
