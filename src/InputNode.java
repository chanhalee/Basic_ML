class InputNode extends Node implements InputNodeInter {
    private static int inputNodeQuantity = 0;

    public InputNode(String name, int serial, double criticalPoint, boolean stimulated) {

        super(name, serial, criticalPoint);
        inputNodeQuantity++;
    }





    @Override
    public boolean transmitSpark() {

        return false;
    }

    @Override
    int getNodeFormat() {
        return NODE_FORMAT;
    }
}