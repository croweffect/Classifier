package at.rovo.classifier.svm.solver.instance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import at.rovo.classifier.svm.kernel.SVCKernel;
import at.rovo.classifier.svm.solver.Solver_NU;
import at.rovo.classifier.svm.struct.Parameter;
import at.rovo.classifier.svm.struct.Problem;
import at.rovo.classifier.svm.struct.SolutionInfo;

public class NuSVC extends SolveInstance
{
	protected static Logger logger = LogManager.getLogger(NuSVC.class.getName());
	
	@Override
	public void solve(Problem prob, Parameter param, double[] alpha, SolutionInfo si)
	{
		int i;
		int l = prob.numInstances;
		double nu = param.nu;

		byte[] y = new byte[l];

		for (i = 0; i < l; i++)
			if (prob.y.get(i) > 0)
				y[i] = +1;
			else
				y[i] = -1;

		double sum_pos = nu * l / 2;
		double sum_neg = nu * l / 2;

		for (i = 0; i < l; i++)
			if (y[i] == +1)
			{
				alpha[i] = Math.min(1.0, sum_pos);
				sum_pos -= alpha[i];
			}
			else
			{
				alpha[i] = Math.min(1.0, sum_neg);
				sum_neg -= alpha[i];
			}

		double[] zeros = new double[l];

		for (i = 0; i < l; i++)
			zeros[i] = 0;

		Solver_NU s = new Solver_NU();
		s.solve(l, new SVCKernel(prob, param, y), zeros, y, alpha, 1.0, 1.0, param.eps, si, param.shrinking);
		double r = si.r;

		if (logger.isDebugEnabled())
			logger.debug("C = " + 1 / r + "\n");

		for (i = 0; i < l; i++)
			alpha[i] *= y[i] / r;

		si.rho /= r;
		si.obj /= (r * r);
		si.upper_bound_p = 1 / r;
		si.upper_bound_n = 1 / r;
	}
}