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

-------------

# Redis

### window10 환경에서 설치
MS Open Tech 그룹에서 포팅한 Windows 용 Redis 는 3.0 버전을 마지막으로 더 이상 업데이트되지 않습니다.  
그래서 https://github.com/tporadowski/redis 에서는 새로운 redis 를 Windows 에 지속적으로 포팅하고 있으니 Windows 에서 Redis 를 사용하려면 이 제품을 사용하는 것이 좋습니다.  


</br>
</br>

-------------

# 더 생각해보기

### 1. save() 메서드를 명시적으로 호출 vs 호출하지 않음  

#### 부제 : JPA Update(수정) 시 save() 메서드를 호출하는 것이 좋을까?  
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

</br>
</br>

-------------

### 2. save() vs saveAndFlush()
save() 메소드를 사용하게 된다면 데이터베이스에 바로 flush 가 되는것이 아니기 때문에,  
synchronized 를 이용한 방법을 테스트할 때 오류가 발생합니다.  

</br>

그 이유는 @Transactional 의 동작방식때문에 그렇습니다.  
(DB 에 값이 입력되기전에 다른스레드가 메소드에 접근이 가능해집니다)

</br>

SynchronizedFacade 를 만들어서 한번 더 래핑해준다면  
save() 메소드를 사용하는편이 더 좋을것 같습니다.  

</br>
</br>
</br>





