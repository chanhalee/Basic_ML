class InputNode extends Node implements InputNodeInter {
    private static int inputNodeQuantity = 0;

    public InputNode(int level, String name, double criticalPoint, boolean stimulated) {

        super(level, name, criticalPoint, NODE_FORMAT);
        inputNodeQuantity++;
    }

    private void activate() {
        data.active = true;
        data.activeCounter++;
    }

    @Override
    void deActivate() {
        data.active = false;
    }


    @Override
    public boolean transmitSpark() {

        return false;
    }
}