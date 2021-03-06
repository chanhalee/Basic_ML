Basic_ML
=============
Fire Together? Wire Together! -stimulated by Breakwater model
-----------------------------
## <실행 환경>
#### Language:   JAVA 16.02
#### OS:         MacOS 11.5.2
#### IDE:        IntelliJ 2021.2


## <프로젝트 배경>
2021년 02월 컴퓨터 공학부 전과를 위해 개인 프로젝트를 진행했었다.
개론 수준에도 미치지 못하는 얼치기 지식과 심리학입문 이라는 교양수업에서 알게된
뉴런의 fire together wire together 이라는 뉴런의 피드백 과정을 조합하여
심리학의 강화학습을 개론 서적에 나오는 인공신경망 회로에 합쳐보는게 어떨까 생각했다.
인공지능에 대해 제대로 공부하고 시작했으면 지금보다 우아한 결과가 나왔겠지만, 당시는 시간도 부족했고
프로젝트에서 어필하려는 것 또한 내가  인공지능 개념을 얼마나 알고 있는가가 아닌 정식으로 컴퓨터공학을 배우진 않았지만,
전과를 시켜주면 잘 따라갈 수 있다는 것과 독특한 아이디어를 만들 수 있다는 것을 어필하기 위해 무작정 진행한
첫 프로젝트를 c 언어로 우여곡절 끝에 완성했고, 전과도 성공했다.

이전 프로젝트 [링크](https://github.com/lee-chanah/Model-of-violent-situatin-encroaching-10yo-boy-s-mind.git)

 반년이 지난 지금, 객체지향 언어인 java를 접하고, 그 때 나름 심혈을 기울였던 프로젝트를 java 로 구현하면 어떻까?
라는 생각이 들어 몇가지 수정을 거치어 구현하였다.
 Java 로 진행한 첫 프로젝트인 만큼, 관용적으로 사용하는 코드들이나 변수명 등 미숙한점이 많고,
머릿속에 있는 개념을 인공지능 개론도 배우지 않은 내가 표현하려다  보니 용어도 상당히 다채롭다.
 그러나 이번학기에 JAVA프로그래밍 과 인공지능 수업을 수강신청 하였기에 동계 방학까지 이론적인 보강을 
할 수 있을 것으로 기대하고, 다음 방학에 있을 활발한 개조에 대한 기반공사를 한 것으로 생각하겠다.


## <도해>
![프로젝트 도해](https://github.com/lee-chanah/Basic_ML/blob/NoneLevelCircuit/docs/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%20%EB%8F%84%ED%95%B4-Breakwater.png)

![출력예시1](https://github.com/lee-chanah/Basic_ML/blob/NoneLevelCircuit/docs/%EC%8B%A4%ED%96%89%20%EC%98%88%EC%8B%9C1.gif)
> 출력 예시1


![출력예시2](https://github.com/lee-chanah/Basic_ML/blob/NoneLevelCircuit/docs/%EC%8B%A4%ED%96%89%20%EC%98%88%EC%8B%9C2.gif)
> 출력 예시2

## 1. 구성


Entity > Circuit > Level > Node > Edge
#### 1. Entity: 하나의 인격체
            향후 인격체간 상호작용을 염두해두고 만들었다. 현재는 인격체 내부의 사고회로안에서만 상호작용이 일어나는 형태로
            
            Entity간 상호작용은 구현되지 않았다.
            
#### 2. Circuit: 사고회로 (인격체에 포함)

            입력->처리->출력 의 구성을 갖는다. 
            
            초기의 프로젝트에선 Level 에 따라 순차적으로 진행되는 구조였으나
            
            level 을 역행하거나 횡보하는 엣지(LoopEdge)를 구상하게되며 레벨이 없는 입력, 처리, 출력의 3단계만 있는 구성으로 하려 했으나
            
            뒤에 나온 회로 구상안에 나와있는 여러 문제점이 예상되어 Level을 진행의 경향성을 나타내도록 남겨두었다.
            
#### 3. Level: 회로의 경향성을 표현하는 관념적 틀
        (2021.11.02 추가 된 내용)
        
        노드들의 집합으로 이루어져 있으며 실무적으로 하는일은 많지만, 회로의 동작에 Level이라는 개념은 단순히 이동 경향성의 표현 수단에 그친다.
        
        기존 Entity가 회로를 직접 메니징 하며, 자료구조등이 헝클어져 가독성이 매우 떨여졌었는데 (None_Level_Circuit 브랜치에 보존되어 있다.) 
        
        Level 클래스에 회로 관리에 필요한 자료들을 결집시켜 자료구조를 획일화 하고, 회로의 동작에 필요한 대부분의 자료를 보관, 관리하는 역할을 부여하였다.
        
        

#### 3. Node: 사고회로의 노드
        <InputNode>, <ProcessNode>, <OutputNode> 로 나뉜다.
        
        InputNode: InputQueue 로 부터 자극을 전달받아 ProcessNode 로 전달한다.
        
        ProcessNode: InputNode 의 엣지를 통해 가중치를 곱한 자극들을 입력받고, 다양한 엣지에 자극을 재전파한다.
        
        OutputNode: 전파되고 재생산된 자극들의 종착역. 다른 노드로 통하는 엣지는 없고, 회로의 진행결과를 표현하는 역할을 한다.
        
#### 4. Edge: 노드에서 다른 노드에 자극을 전달하기 위한 교각.
        <LoopEdge> 와 루프를 형성하지 않는 <Edge> 로 나뉜다.
        
        Edge: 출발 노드(소속 노드)의 흥분시 활성화 되며, 도착 노드에 가중치를 적용한 자극을 전달한다.
        
        LoopEdge: Edge 와 작동방식을 비슷하나 차이점은 Edge 는 무한루프를 만들 가능성이 없지만, LoopEdge는 무한루프를 만들가능성이 있다.
                  따라서 한번의 인풋에 활성화 되는 횟수에 상한을 적용한다.
#### E. 기타
totalQueue > Cycle > Tick <br>
= 전체 시행 > 1회기 > 틱

>        Tick: 노드의 흥분여부에 따라 Edge를 통해 다음노드로 자극이 전달되어 다음노드가 흥분여부를 결정하기까지의 시행을 틱 이라고 한다.
>        즉 선행 노드의 흥분이 후행노드에 전달되어 흥분여부를 결정하기까지의 시행을 틱이라고 한다.
> 
>        2가지 구현 방식이 고려 되었다. 
>        1. initial Level -> final Level 까지 순차적으로 탐색하여 이전 level 엣지의 자극들이 같은 틱 내에서 영향력을 행사할 수
>        있는 방식. 즉 노드가 낮은 레벨에서부터 순차적으로 발화하여 당tick 내에 선행 노드의 발화 영향이 후행에 전달되는 방식.
>        2. 이번 틱의 영향으로 증가한 자극들은 기존의 자극들이 저장된 계좌와 별개로 저장되는 방식, 노드의 발화가 Level순서와 관계없이 
>        동시에 발회되는 방식.
> 
>        1번째 방식으로 하면 본대는 1틱만에 outputNode에 도착하여, 루프엣지가 몰고오는 잔파도의 의미가 퇴색될 것이다.
>        따라서 2번째 방법으로 Tick을 진행 하였다.

>        Cycle: 인풋노드에 소스가 주어지고, 회로내 모든 노드가 흥분하지 않을때까지의 활동 기간을 Cycle이라고 한다.

>        totalQueue: 파일을 통해 인풋 노드에 여러차례 입력이 주어지는데 그 입력들에 대한 모든 시행이 종료되기까지를 전체 시행,



## 2. 노드의 흥분 매커니즘


Node.InputNode 를 제외한 노드들은 하위 레벨노드의 흥분으을 엣지를 통해 편향된 값을 전달 받으며 이 값들은 음수도, 양수도 가능하다.

노드의 흥분 역치는 양의 값을 가지며, 동 레벨 내에서 편차가 크지 않도록 신경쓴다.

엣지가 전달하는 자극들을 합산해 자극을 전달받은 노드가 흥분을 결정하는 규칙의 후보는 2개이다.

(기존에 계획되지 않았던 역방향 전달을 구현하게되어 도미노식 자극 전달을 극복해야 했다.)

### <후보 1> 민주주의 모델
노드의 흥분은 하위 노드로부터 엣지를 통해 들어온 input 들의 평균을 기준으로 결정한다.

루프 엣지를 구현하는 것이 불가능하여 부적합


### <후보 2> 방파제 모델 - (확정)
#### 배경
기존 회로(2021.02 버전)에 루프엣지를 구현할 경우 루프엣지로 인해 촉발된 자극들은 일반 엣지가 발생시킨 자극들보다 output 까지 도달시간이 지연된다.   
이는 자극들이 섞이지 않게하고, 마치 방파제에 작은 파도들이 힘없이 부서지는 것처럼 자극들의 자극간의 시너지를 불가능하게한다.   
선발대의 자극들을 지연시키고, 힘을 모아 아웃풋을 함께 공략할 쓰나미를 만들기 위해 노드를 흥분시키지 못한 자극들에 유예를 부여하는 방파제 모델을 구상하였다.   
   
#### 회로 청사진
엣지로부터 들어온 input 의 반감기는 2회기 이다. 노드는 input 이 들어오면 2회기동안 보관하다가 기한이 지나면 반감시킨다.   
저장된 흥분과 새로 들어온 input 의 합이 역치를 넘을 경우 노드를 흥분시키고, 사용된 자극들을 소멸시킨다.   
레벨이 많아지거나 루프가 많아지면 , 조절 주기와 조절 계수를 조정한다.   
(직관적으론 레벨이 많아지면 뒤로갈 수록 파도가 힘을 유지한체 모이게 되어 조절 주기를 1회기, 계수 0.8 이런식으로 
주기와 계수를 유기적으로 조절하여 후발대의 위력을 줄일 수 있을 것 같다.)   
(루프가 많아진다면 잔파도가 많아진다는 건데 비트코인 발행시간 조절하듯이 한번 흥분한 노드는 방파제를 높이도록 해야할까?)  

> ** 2021.11.01 수정
> 
> 노드가 흥분할 때 계좌의 모든 잔고를 소멸시키지 않고, 역치 만큼의 값을 소거하는 방법으로 바꿨다.
> 
>  소거 순위
> > 1.이번틱에 도착한 자극
> >
> > 2.오래된 자극
> 
> 참고) 소거 시 잔고의 일부를 소거하지 않고, 해당 틱 전부를 소거한다. (노드의 과도한 흥분빈도 억제)
>
>  Ex) -1 틱 보관 자극: 2.13 | 소거액: 1.5, 소거 누계: 1.12 
>    
>  -소거 후: -1 틱 잔고 : 0
   
#### 장점   
output 노드가 흥분을 신중히 결정할 수 있다.   
루프를 거친 자극들이 본대와 함께 방파제를 넘어가서 아웃풋 노드를 함께 공략할 수 있다.   
   
#### 단점   
-
   
#### 필요조건   
엣지의 가중치들이 양의 값을 갖는 경향성이 있어야 한다. (두번째 파도가 음의 파도라면 방파제 역할이 무색해진다.)   
루프엣지의 개수도 적어선 안된다. 자극 할인 계수의 디폴트 값이 0.6인것을 고려한다면, 루프가 적을경우 잔파도들의 영향은 미미할 것으로 생각된다.   


