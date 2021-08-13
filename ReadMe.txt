규칙
1.구성
a. Entity: 하나의 인격체
a.1 circuit: 사고회로
-노드들의 집합
 -circuit 의 자료형은 HashMap<levelData, HashMap /* 주 1 */ >
  <주 1> 각 레벨의 노드들을 담는 해시 맵. 자료형은 HashMap<Node /* 주 1-1*/ , Set /* 주 1-2*/ >
  <주 1-1> Node의 해시코드는 오버라이딩하여 구현
  <주 1-2> Set는 Node와 연결된 다른 Node를 저장.
b. Node: 회로의 구성 개체