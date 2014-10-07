
package infixtopostfix;

public class RigidVarMap<I, O> {
    private I[] vars;
    private O[] nums;
    int numVars;

    public RigidVarMap(int cap) {
        vars = (I[]) new Object[cap];
        nums = (O[]) new Object[cap];
        numVars = 0;
    }

    public void add(I var, O val) {
        vars[numVars] = var;
        nums[numVars++] = val;
    }

    public O valueOf(I var) {
        for (int i=0; i<vars.length; i++) {
            if (var.equals(vars[i])) {
                return nums[i];
            }
        }
        return null;
    }

}
