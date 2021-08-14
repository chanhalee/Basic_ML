// Edge와 LoopEdge에 loopcounter을 만들어 실행뒤 카운터가 남았는지 확인, 실행시 카운터 감소.
// level은 경향성을 나타낸다고 했다. loop을 생성할 수 있다는 것을 알려면 동일한 레벨 또는 하위 레벨로 향하는 엣지는 루프를 형성할 수 있다.
// 따라서 실행 회기에

public class LoopEdge extends Edge implements LoopAble{
    int loop;
    LoopEdge(int start, int dest, double weight, double weighDelta, int loop) {
        super(start, dest, weight, weighDelta);
    }
    LoopEdge(int start, int dest, double weight, int loop) {
        super(start, dest, weight);
    }
}

interface LoopAble{}