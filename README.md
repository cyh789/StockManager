# StockManager
상품 재고 관리시스템  

</br>
</br>

# Commit Message Convention
```
type: Subject

Body

Footer
```  

### Commit Message Convention :: Title
커밋 메시지 Title은 docs: Update README.md처럼 작성한다.  
docs는 type, Update README.md는 Subject이다. 

</br>

### Commit Message Convention :: Subject
Subject는 크게 4가지의 특징을 가진다.  
길이는 50자 이하로 작성한다.  
동사원형(ex. Add, Update, Modify)로 시작한다.  
첫 글자는 대문자이다.  
끝에는 마침표를 붙이지 않는다.  

</br>

### Commit Message Convention :: Type
FEAT: 새로운 기능 추가  
FIX: 버그 수정  
DOCS: 문서 수정  
STYLE: 코드 포맷 변경, 세미콜론 누락, 코드 변경 없음  
REFACTOR: 프로덕션 코드 리팩터링  
TEST: 테스트 추가, 테스트 코드 리팩터링, 프로덕션 코드 변경 없음  
CHORE: 빌드 테스크 업데이트, 패키지 매니저 환경설정, 프로덕션 코드 변경 없음  

</br>
</br>


# Redis

### window10 환경에서 설치

<details>
<summary>접기/펼치기</summary>

MS Open Tech 그룹에서 포팅한 Windows 용 Redis 는 3.0 버전을 마지막으로 더 이상 업데이트되지 않습니다.  
그래서 https://github.com/tporadowski/redis 에서는 새로운 redis 를 Windows 에 지속적으로 포팅하고 있으니 Windows 에서 Redis 를 사용하려면 이 제품을 사용하는 것이 좋습니다.  

</details>

</br>
</br>


# 더 생각해보기

</br>

### 1. save() 메서드를 명시적으로 호출 vs 호출하지 않음  

#### 부제 : JPA Update(수정) 시 save() 메서드를 호출하는 것이 좋을까?  

<details>
<summary>접기/펼치기</summary>

JPA를 사용하면 트랜잭션 범위 안에서 Dirty Checking이 동작한다.  
따라서 save() 메서드를 호출하지 않아도 값이 알아서 수정되고 반영된다.  
그렇다면 save() 메서드를 호출하는 것이랑 어떤 차이가 있는지 알아보자.  

</br>

먼저 @Transactional만을 사용한 예제를 보자.
```java
@Transactional
public Notice update(Long noticeId, String content) {
    Notice notice = noticeRepository.findById(noticeId).get();
    notice.setContent(content);
}
```
다음은 repository.save() 메서드를 사용한 예제를 보자.  

```java
public Notice update(Long noticeId, String content) {
    Notice notice = noticeRepository.findById(noticeId).get();
    notice.setContent(content);
    noticeRepository.save(notice);
}
```
위 두 코드의 최종 상태는 동일하다.  

</br>

1번 코드의 경우 객체가 자기 할일만 하는 코드이고,  
2번 코드의 경우 객체의 관점에서 자신의 상태를 변경한 후에, DB에도 따로 반영을 해주는 코드이다.  
즉, 2번 코드는 외부 인프라인 DBMS를 우려한 코드이고, 객체 지향 관점에서 좋은 형태로 보긴 힘들다.  

##### 테스트 관점
추가로 고려해야할 점이 테스트이다. 사실 이 문제를 고민하게 된 이유도 테스트때문이다.  
Service 클래스에 대한 단위 테스트를 진행하기 위해서는 Repository Mocking이 필요하다.  
응용 서비스단에서 save() 메서드를 의미없이 호출하게 되면, 불필요하게 Mocking할 메서드가 하나 더 늘어난다.  
이는 테스트 코드가 복잡해지는 결과를 초래한다.  

##### 결론
새로운 엔터티를 추가할 때는 repository.save() 메서드 사용을 해야 한다.  
하지만, 기존의 엔터티를 수정하는 작업에서는 repository.save() 메서드를 사용하지 않는 것이 더 깔끔하다!  

</details>
</br>

-------------

### 2. save() vs saveAndFlush()

<details>
<summary>접기/펼치기</summary>

save() 메소드를 사용하게 된다면 데이터베이스에 바로 flush 가 되는것이 아니기 때문에,  
synchronized 를 이용한 방법을 테스트할 때 오류가 발생합니다.  

</br>

그 이유는 @Transactional 의 동작방식때문에 그렇습니다.  
(DB 에 값이 입력되기전에 다른스레드가 메소드에 접근이 가능해집니다)

</br>

SynchronizedFacade 를 만들어서 한번 더 래핑해준다면  
save() 메소드를 사용하는편이 더 좋을것 같습니다.  

</details>
</br>

-------------

### 3. Pessimistic Lock vs Optimistic Lock

<details>
<summary>접기/펼치기</summary>
    
충돌이 적은 경우 optimistic lock 이 빠르지만,  
충돌이 많다면 pessimistic lock 이 더 빠르므로, 경우에 따라 다릅니다.  

다만, 본인이 실제 서비스에 적용한다면 optimistic lock을 우선 고려 할 것 같습니다.  
(pessimistic lock은 데드락을 고려해야 하므로)  

</details>
</br>

<details>
<summary>접기/펼치기</summary>

https://github.com/cyh789/wanted-pre-onboarding-challenge-be-task-July/blob/main/1%EB%B2%88%20%EB%AC%B8%EC%A0%9C.md

</details>

-------------

### 4. Facade? Helper?

<details>
<summary>접기/펼치기</summary>

Facade는 내부 로직을 캡슐화하는 디자인 패턴.  
사실 우리 구현사항에서 Facade에는 락을 얻는 행위만 있으므로 다른 패턴이 더 적합할 수 있지만, 구현이 매우 쉬워서 실무에서 자주 쓰는 편이다.

</details>
</br>

-------------

### 5. MySQL? Redis?

<details>
<summary>접기/펼치기</summary>
    
이미 MySQL 을 사용하고 있다면 별도의 비용 없이 사용가능하다.  
어느 정도의 트래픽까지는 문제 없이 활용이 가능하다. 하지만 Redis 보다는 성능이 좋지 않다.  
만약 현재 활용중인 Redis 가 없다면 별도의 구축비용과 인프라 관리비용이 발생한다. 하지만, MySQL 보다 성능이 좋다.

</details>
</br>

-------------

### 6. namedLock에서 @Transactional(Propagation.REQUIRES_NEW) 를 사용하는 이유가 무엇인가요?  

<details>
<summary>접기/펼치기</summary>

부모의 트랜잭션과 동일한 범위로 묶인다면 Synchronized 와 같은 문제가 발생합니다.  
Database 에 commit 되기전에 락이 풀리는 현상이 발생합니다.  

</br>

그렇기 때문에, 별도의 트랜잭션으로 분리를 해주어 Database 에 정상적으로 commit 이 된 이후에 락을 해제하는것을 의도하였습니다.  

</br>

핵심은 lock 을 해제하기전에 Database 에 commit 이 되도록 하는것입니다.  

</details>
</br>

-------------














</br>
</br>

-------------

# 참고링크

<details>
<summary>접기/펼치기</summary>

</br>

##### 프로젝트 진행중 로직 및 이론 참고

</br>

https://github.com/Hyune-s-lab/manage-stock-concurrency 

</br>

https://dev-alxndr.tistory.com/45  
https://github.com/dev-alxndr/concurrency-stock/blob/main/src/main/java/me/alxndr/conccurecystock/facade/RedissonLockStockFacade.java  

</br>

https://sigridjin.medium.com/weekly-java-%EA%B0%84%EB%8B%A8%ED%95%9C-%EC%9E%AC%EA%B3%A0-%EC%8B%9C%EC%8A%A4%ED%85%9C%EC%9C%BC%EB%A1%9C-%ED%95%99%EC%8A%B5%ED%95%98%EB%8A%94-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%9D%B4%EC%8A%88-9daa85155f66  
</br>

https://studyhardd.tistory.com/87

</br>

https://thalals.tistory.com/370  
https://github.com/thalals/ConcurrencyIssue-lab  
</br>

https://velog.io/@coconenne/%EC%8A%A4%ED%94%84%EB%A7%81%EC%9C%BC%EB%A1%9C-%EC%95%8C%EC%95%84%EB%B3%B4%EB%8A%94-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%9D%B4%EC%8A%88  
https://github.com/issiscv/concurrency-Issue-wtih-spring  

</br>
</br>

##### JPA - Update(수정) 시 save() 메서드를 호출하는 것이 좋을까?  
https://jaehoney.tistory.com/273  

</br>

##### namedLock에서 @Transactional(Propagation.REQUIRES_NEW) 를 사용하는 이유가 무엇인가요?  
https://www.inflearn.com/questions/633358/propagation-requires-new-%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EB%8A%94-%EC%9D%B4%EC%9C%A0%EA%B0%80-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80%EC%9A%94  

</br>

##### 서비스 로직에서 @Transactional vs save() vs saveAndFlush() 질문입니다.  
https://www.inflearn.com/questions/655574/%EC%84%9C%EB%B9%84%EC%8A%A4-%EB%A1%9C%EC%A7%81%EC%97%90%EC%84%9C-saveandflush-%EC%A7%88%EB%AC%B8%EC%9E%85%EB%8B%88%EB%8B%A4  

</br>


</details>

























