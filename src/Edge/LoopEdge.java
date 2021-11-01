package Edge;// Edge와 LoopEdge에 loopcounter을 만들어 실행뒤 카운터가 남았는지 확인, 실행시 카운터 증가.
// 생성시 루프 횟수를 뜻하는 LOOP_COUNT 를 입력받는다.
// level은 경향성을 나타낸다고 했다. loop을 생성할 수 있다는 것을 알려면 동일한 레벨 또는 하위 레벨로 향하는 엣지는 루프를 형성할 수 있다.
// 따라서 실행 회기에

import Edge.Interfaces.LoopAble;

public class LoopEdge extends Edge implements LoopAble {
    final int LOOP_COUNT;

    public LoopEdge(int start, int dest, double weight, double weighDelta, int loop) {
        super(start, dest, weight, weighDelta);
        this.LOOP_COUNT = loop;
    }
    public LoopEdge(int start, int dest, double weight, int loop) {
        super(start, dest, weight);
        this.LOOP_COUNT = loop;
    }
    @Override
    public void activate(){    // 속한 노드가 흥분했을 경우 실행
        if(checkVital()) {
            activeCounter++;
            followingNodeAccount.addStimulus(weight);
            weight += weightDelta;
        }
    }

    @Override
    public boolean checkVital(){
        return (activeCounter < LOOP_COUNT);
    }

    @Override
    public String toString(){
        return "["+START_NODE_SERIAL+"] ["+DESTINATION_NODE_SERIAL+"] ["+weight +"] ["+ LOOP_COUNT +"]";
    }

}

