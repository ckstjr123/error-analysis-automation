# 에러 분석 자동화
LLM(Large Language Model)을 통해 에러에 대한 원인 파악과 해결 방법을 제시합니다.  
불친절한 에러 메시지, 원인을 알기 힘든 라이브러리 예외 등으로 인한 에러 해결 비용을 줄입니다.

<br/>

주요 기능
---
- `@RestControllerAdvice`와 결합된 AOP를 통해 애플리케이션 내에서 전파된 예외를 전역 추적합니다. 커스텀 어노테이션을 적용해서 민감하거나 분석이 불필요한 예외를 대상에서 제외할 수 있습니다.
- 런타임에 라인 번호 및 메서드 시그니처를 추출하여 LLM에 더욱 구체적인 맥락을 제공합니다. 또한 필요한 스택 트레이스만을 필터링함으로써 분석 효율성을 높이고 토큰 비용을 절감합니다.
- Flowise(LLM 워크플로우 빌더)를 통해 API 명세를 안내하여 요청을 특정하고 정형화된 에러 분석 결과를 응답하도록 합니다.  
  요청 정보를 포함하여 분석이 완료된 에러를 알림으로 공유합니다.

<br/>

사용 기술 / 라이브러리
---
<p>
  <img src="https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/Spring_Boot_3.5.11-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Spring_Cloud_Feign-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Feign"/>
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis"/>
  <img src="https://img.shields.io/badge/ASM_9.7-000000?style=for-the-badge&logo=assemblyscript&logoColor=white" alt="ASM"/>
  <img src="https://img.shields.io/badge/Flowise-3178C6?style=for-the-badge&logo=flowiseai&logoColor=white" alt="Flowise"/>
  <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white" alt="Slack"/>
</p>

<br/>

동작 흐름
---
<img width="8192" height="3591" alt="sequence_diagram" src="https://github.com/user-attachments/assets/58d5cb5e-0ff9-49a0-9a67-42b0c74f0f14" />

<br/>

실행 예시
---
*<img width="681" height="205" alt="image" src="https://github.com/user-attachments/assets/f2947cde-57bd-4b12-9847-dbd1fbd0d430" />*

<br/>

참고 자료
---
- [Reference](https://jhzlo.tistory.com/83)
- [Flowise Documentation](https://docs.flowiseai.com/)