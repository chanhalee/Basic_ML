
/* ---------------------------------------------
## 목차
1. 개체의 기본형
2.* 개체에 관련된 데이터 저장소
2.1. LevelData
3.* 개체의 동작에 필요한 클래스
3.1.파일 입출력 관련 함수

----------------------------------------------- */
//
//개체에 기본형을 만든 이유는 기본형을 상속한 여러 성격의 객체를 구현할 계획이 있기 때문이다.
// ex) 덤벙대는 개체, 예민한 개체, 우두머리 개체 등..
//
//
//
//
//

import jdk.jshell.spi.ExecutionControl;

import java.util.*;
/*-------------------------------1.개체의 기본형-------------------------------*/

public class Entity {
    private HashMap<IdentityData, HashMap> mindCircuit = null;

    Entity(ArrayList<String> inputFileList, ArrayList<String> outputFileList){


    }

}
























/*-------------------------------2.*개체에 관련된 데이터 저장소-------------------------------*/

/*-------------------------------2.1.LevelData-------------------------------*/

class LevelData{
    private final int LEVEL;

    LevelData(int level) {
        this.LEVEL = level;
    }

    @Override
    public int hashCode() {
        int result = (int) (LEVEL * 1000);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof LevelData) {
            LevelData that = (LevelData) obj;
            if (this.hashCode() == that.hashCode())
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "" + this.hashCode();
    }

}


/*-------------------------------3.* 개체의 동작에 필요한 클래스-------------------------------*/

/*-------------------------------3.1.파일 입출력 관련 클래스-------------------------------*/
class IOHelper{

}