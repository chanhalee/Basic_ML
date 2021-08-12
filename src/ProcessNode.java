public class ProcessNode extends Node implements ProcessNodeInter {

    ProcessNode(int level, String name, double criticalPoint) {
        super(level, name, criticalPoint, NODE_FORMAT);
    }

    @Override
    void deActivate() {
        data.active = false;
    }

    @Override
    public String toString() {
        return "[Lv" + data.NODE_LEVEL + " " + data.NAME + " -CP " + data.criticalPoint + " -AC " + data.activeCounter + " -Hash " + data.getIdentity() + "]";
    }

    @Override
    public boolean transmitSpark() {

        return false;
    }
}
