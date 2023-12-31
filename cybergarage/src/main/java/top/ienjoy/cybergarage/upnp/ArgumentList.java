package top.ienjoy.cybergarage.upnp;

import java.util.Vector;

public class ArgumentList extends Vector<Argument> {
    ////////////////////////////////////////////////
    //	Constants
    ////////////////////////////////////////////////

    public final static String ELEM_NAME = "argumentList";

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public ArgumentList() {
    }

    ////////////////////////////////////////////////
    //	Methods
    ////////////////////////////////////////////////

    public Argument getArgument(int n) {
        return get(n);
    }

    public Argument getArgument(String name) {
        int nArgs = size();
        for (int n = 0; n < nArgs; n++) {
            Argument arg = getArgument(n);
            String argName = arg.getName();
            if (argName == null)
                continue;
            if (argName.equals(name))
                return arg;
        }
        return null;
    }

    /**
     * Set all the Argument which are Input Argoument to the given value in
     * the argument list
     */
    public void setReqArgs(ArgumentList inArgList) {
        int nArgs = size();
        for (int n = 0; n < nArgs; n++) {
            Argument arg = getArgument(n);
            if (arg.isInDirection()) {
                String argName = arg.getName();
                Argument inArg = inArgList.getArgument(argName);
                if (inArg == null)
                    throw new IllegalArgumentException("Argument \"" + argName + "\" missing.");
                arg.setValue(inArg.getValue());
            }
        }
    }

    /**
     * Set all the Argument which are Output Argoument to the given value in
     * the argument list
     */
    public void setResArgs(ArgumentList outArgList) {
        int nArgs = size();
        for (int n = 0; n < nArgs; n++) {
            Argument arg = getArgument(n);
            if (arg.isOutDirection()) {
                String argName = arg.getName();
                Argument outArg = outArgList.getArgument(argName);
                if (outArg == null)
                    throw new IllegalArgumentException("Argument \"" + argName + "\" missing.");
                arg.setValue(outArg.getValue());
            }
        }
    }
}

