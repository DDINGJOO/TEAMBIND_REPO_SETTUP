# TEAMBIND_REPO_SETTUP 템플릿 사용 가이드

이 저장소는 다음을 자동화하는 GitHub 템플릿입니다.
- feature/* 브랜치에 push하면 자동으로 PR을 생성/업데이트(요약 포함)
- PR이 열리거나 업데이트되면 파일 경로에 따라 라벨 자동 부여
- 일관된 PR 템플릿 제공
- 이슈 폼(Epic/Story/Task/Spike/Change Request) 제공

아래 가이드는 "A 하면 -> B 된다" 형태로 처음부터 끝까지 따라 할 수 있도록 구성되어 있습니다.

## 0. 전제 조건
- GitHub Actions가 활성화된 퍼블릭/프라이빗 저장소
- 기본(base) 브랜치가 `main`인 저장소
- 저장소에 Actions 권한이 기본값(읽기/쓰기)로 설정됨

참고: `auto-pr.yml`은 GitHub CLI 액션(cli/cli-action@v2)을 사용하며, 기본 제공되는 `${{ github.token }}`으로 동작합니다. 별도 토큰이 필요 없습니다.

## 1. 템플릿로 저장소 만들기
1) GitHub에서 이 저장소 페이지 상단의 "Use this template" 버튼 클릭
2) 새 저장소 이름을 입력하고 "Create repository" 클릭
3) 생성된 저장소의 Settings → Actions → General에서 Workflow permissions가 기본값인지 확인

- 이렇게 하면 -> 초기 상태에서 다음 파일들이 포함됩니다:
  - .github/workflows/auto-pr.yml
  - .github/workflows/auto-label.yml
  - .github/labeler.yml
  - .github/pull_request_template.md
  - .github/ISSUE_TEMPLATE/* (epic.yml, story.yml, task.yml, spike.yml, change_request.yml)

## 2. 브랜치 전략과 트리거
- feature/* 브랜치에 push될 때 Auto PR가 동작합니다.
- PR에서 파일 경로가 특정 패턴과 매칭되면 Auto Labeler가 라벨을 붙입니다.

예시:
- "feature/user-login" 브랜치에서 커밋을 push 하면 -> base: main 대상으로 PR이 자동 생성(또는 업데이트)됩니다.
- PR에 `src/main/java/**` 파일 변경이 포함되면 -> `backend` 라벨이 자동으로 붙습니다.
- PR에 `.github/**` 파일 변경이 포함되면 -> `infra` 라벨이 자동으로 붙습니다.
- PR에 `docs/**` 파일 변경이 포함되면 -> `docs` 라벨이 자동으로 붙습니다.

## 3. Auto PR 동작 상세 (.github/workflows/auto-pr.yml)
- 트리거: `push` on `feature/**`
- 기본 브랜치: `main` (워크플로에서 `BASE=main`으로 설정)
- 수행 작업:
  1) 저장소 체크아웃
  2) GitHub CLI 설치
  3) `origin/main` 대비 현재 HEAD의 변경 요약을 SUMMARY.md로 생성
     - 커밋 목록
     - 변경 파일 통계 (added/changed/deletions)
     - 주요 변경 파일 (최대 50개)
  4) 동일 head/base 조합의 오픈 PR이 있으면 -> PR 제목/본문을 업데이트
  5) 없으면 -> 새 PR을 생성
- PR 제목: 최신 커밋 메시지
- PR 본문: PR 템플릿(.github/pull_request_template.md) + 자동 생성 요약(SUMMARY.md)을 결합한 내용

즉, "feature/* 브랜치에 push" 하면 -> "요약이 포함된 PR이 자동 생성/갱신" 됩니다.

주의:
- 기본 브랜치가 `main`이 아닌 경우 -> `auto-pr.yml`의 `Prepare base` 스텝에서 `BASE` 값을 수정하세요.
- 모노레포 등에서 베이스를 변경해야 한다면 -> 환경변수 `BASE`를 원하는 브랜치로 변경하세요.

## 4. Auto Labeler 동작 상세 (.github/workflows/auto-label.yml, .github/labeler.yml)
- 트리거: `pull_request_target` (opened, synchronize)
- 권한: `pull-requests: write`
- 라벨 규칙(.github/labeler.yml):
  - `backend`: `src/main/java/**`
  - `docs`: `docs/**`
  - `infra`: `.github/**`

즉, "PR을 열거나 커밋을 추가" 하면 -> "변경 파일 경로에 따라 위 라벨이 자동으로 붙음".

보안 주의:
- `pull_request_target`는 포크 PR에 대해 기본 저장소 컨텍스트에서 실행됩니다. 이 워크플로우는 외부 액션(official verified action)만 사용하고, 코드 체크아웃 없이 labeler 액션만 돌기 때문에 상대적으로 안전합니다. 그래도 민감한 시크릿을 노출하거나 임의 스크립트를 실행하지 않도록 유지하세요.

라벨 규칙 커스터마이징:
- `.github/labeler.yml`에 매핑을 추가/수정하면 됩니다. 예:
  - `frontend`: `src/main/frontend/**`

## 5. PR 템플릿 (.github/pull_request_template.md)
- PR 작성 시 자동으로 본문에 템플릿이 채워집니다.
- 구조:
  - 제목: `<type(scope): summary> refs #ISSUE`
  - 목적/변경 요약/수용 기준 검증/브레이킹/테스트/참조 등

즉, "PR 화면을 열면" -> "일관된 체크리스트 기반 템플릿으로 품질을 관리"할 수 있습니다.

## 6. 이슈 템플릿 (.github/ISSUE_TEMPLATE/*)
- 제공 템플릿: Epic, Story, Task, Spike, Change Request
- 새 이슈 생성 시 템플릿을 선택할 수 있습니다.

바로 쓰는 방법(처음 사용자용):
1) GitHub 저장소 상단의 Issues 탭 진입
2) New issue 버튼 클릭
3) 보이는 템플릿 카드 중 하나를 선택(Epic/Story/Task/Spike/Change Request)
4) 화면 우측(또는 하단)에 나타나는 폼을 각 항목 설명대로 작성
5) Submit new issue 클릭

A 하면 -> B 된다 예시(각 템플릿별로 폼에 무엇이 보이는지):
- Epic 템플릿 선택하면 -> 다음 필드가 보입니다.
  - 목표(필수): 해결하려는 문제/성공지표를 적습니다.
  - 범위/Not-in-scope: 포함/제외 범위를 구분합니다.
  - 디자인/문서 링크: 관련 문서 URL을 넣습니다.
  - 하위 스토리(체크리스트): `- [ ] Story 1`처럼 체크박스로 쪼갭니다.
  - 마일스톤: 연결할 마일스톤 이름을 적습니다.
- Story 템플릿 선택하면 -> 다음 필드가 보입니다.
  - 배경: 컨텍스트/요구사항 배경을 적습니다.
  - 수용 기준(AC)(필수): `- [ ] ...` 형식으로 검증 가능한 체크리스트를 작성합니다.
  - 디자인/계약 링크: API 계약/화면 설계 등 링크를 적습니다.
  - 구현 메모/리스크: 기술적 고려/리스크를 메모합니다.
  - 연결된 Epic: 관련 Epic 이슈 번호(예: #123)를 적습니다.
- Task 템플릿 선택하면 -> 다음 필드가 보입니다.
  - 연결된 Story/Epic: 부모 이슈 번호를 적습니다.
  - 작업 범위(필수): 해야 할 일을 구체적으로 적습니다.
  - Done 기준(체크박스): 테스트/문서/CI/리뷰 항목을 완료 시 체크합니다.
- Spike 템플릿 선택하면 -> 다음 필드가 보입니다.
  - 타임박스(필수): 1d, 4h 등 시간을 명시합니다.
  - 핵심 질문: 무엇을 검증/학습할지 적습니다.
  - 접근 방법: 조사/실험 계획을 적습니다.
  - 산출물: 결과 요약/ADR/POC 링크를 적습니다.
- Change Request 템플릿 선택하면 -> 다음 필드가 보입니다.
  - 영향받는 Epic/Story/Task: 관련 이슈 번호를 나열합니다.
  - 제안 변경 사항(필수): 무엇을 어떻게 바꿀지 적습니다.
  - 영향도: BE/FE/DB/마이그레이션 등 파급범위를 적습니다.
  - 결정/대안/근거: 최종 결정과 근거(ADR 링크 등)를 남깁니다.

어디에 저장되어 있나요?
- 실제 템플릿 파일들은 `.github/ISSUE_TEMPLATE/` 폴더의 YAML들입니다.
- 필요 시 이 파일들을 열어 레이블/기본 텍스트/필드를 자유롭게 수정할 수 있습니다.

자주 하는 착각/팁:
- 빈 이슈(Blank issue)는 기본적으로 비활성화되어 있어 템플릿 중에서 선택해야 합니다.
- 체크박스는 `- [ ]` 형태를 유지해야 UI에서 체크로 표시됩니다.
- Epic → Story → Task 순으로 링크(#이슈번호)로 연결하면 추적성이 좋아집니다.

## 7. 처음부터 끝까지 예시 시나리오
1) 개발자는 `feature/user-login` 브랜치를 생성하고 커밋을 push한다.
   - 그러면 -> GitHub Actions가 실행되어 `origin/main` 대비 변경 요약을 만들고 PR을 자동 생성한다.
   - PR 제목은 마지막 커밋 메시지, 본문에는 커밋/변경 통계/변경 파일 리스트가 들어간다.
2) 리뷰어가 PR을 열어본다.
   - 그러면 -> PR 템플릿 섹션(목적/AC/테스트 등)이 보이며, 작성자는 이를 채울 수 있다.
3) 추가 커밋을 같은 브랜치에 push한다.
   - 그러면 -> 기존 PR이 자동으로 업데이트되어 요약이 최신 상태로 바뀐다.
4) PR에 `.github/**` 파일이 수정되었다.
   - 그러면 -> `infra` 라벨이 자동으로 붙는다.
5) Epic/Story/Task를 계획한다.
   - 그러면 -> 새 이슈 생성 화면에서 해당 템플릿을 선택해 필요한 정보를 구조적으로 입력할 수 있다.

## 8. 커스터마이징 포인트
- 기본 브랜치 변경: `.github/workflows/auto-pr.yml`의 `BASE=main` 값을 수정
- 트리거 브랜치 변경: `on.push.branches`에서 `feature/**` 패턴을 다른 규칙으로 변경
- 요약 포맷 변경: `Generate summary` 스텝의 쉘 스크립트 수정 (커밋 포맷, 통계, 파일 리스트 등)
- 라벨 규칙: `.github/labeler.yml` 수정/추가
- PR 템플릿 문구: `.github/pull_request_template.md` 수정
- 이슈 템플릿 폼: `.github/ISSUE_TEMPLATE/*.yml` 수정
- PR 본문을 특정 경로의 MD로 고정:
  - 방법 A(권장): 저장소 Settings → Actions → Variables에서 `PR_BODY_PATH` 변수를 추가하고 원하는 파일 경로(예: `docs/release-notes.md`)를 값으로 설정하면 -> 해당 파일이 PR 본문으로 사용됩니다.
  - 방법 B: 레포에 `.github/PR_BODY.md` 파일을 만들면 -> 그 파일이 PR 본문으로 사용됩니다.
  - 방법 C: 레포에 `.github/auto-pr/body.md` 파일을 만들면 -> 그 파일이 PR 본문으로 사용됩니다.
  - 위 파일/변수가 없을 때만 -> 기존 동작(템플릿 + 자동 요약)에 폴백합니다. 잠금 마커(`<!-- auto-pr: lock -->`) 동작은 동일합니다.

## 9. 자주 하는 질문(FAQ)
Q. 베이스 브랜치가 develop인데요?
- A. `auto-pr.yml`에서 `BASE=develop`로 바꾸면 -> 모든 요약/PR 대상이 develop 기준으로 바뀝니다.

Q. feature가 아니라 hotfix 브랜치에서도 PR 자동 생성하고 싶어요.
- A. `on.push.branches`에 `hotfix/**`를 추가하면 -> hotfix 브랜치 push에도 PR이 자동 생성됩니다.

Q. 포크 저장소의 PR에도 라벨이 붙나요?
- A. 네. `pull_request_target` 이벤트로 열리거나 동기화될 때 -> labeler가 작동하여 라벨을 붙입니다.

Q. PR 내용을 자동으로 작성해주나요?
- A. 기본값은 "PR 템플릿 + 자동 요약"을 합쳐서 작성합니다. 다만 특정 경로의 MD 파일을 그대로 본문으로 쓰고 싶다면 아래 중 하나를 설정하세요: 1) 저장소 Actions Variable로 `PR_BODY_PATH`를 설정(예: `docs/release-notes.md`), 2) `.github/PR_BODY.md` 파일 추가, 3) `.github/auto-pr/body.md` 파일 추가. 이런 파일/변수가 있으면 -> 해당 파일 내용이 PR 본문으로 사용되고 자동 요약은 본문에 붙지 않습니다.

Q. 자동으로 생성된 PR 내용이 마음에 안 들면 어떻게 수정하나요?
- A. PR 설명을 직접 수정한 뒤, 본문 어딘가에 다음 마커를 한 줄로 추가하세요:
  - `<!-- auto-pr: lock -->`
- 이렇게 하면 -> 이후 같은 브랜치로 push 되어도 워크플로우가 PR 제목/본문을 더 이상 덮어쓰지 않습니다. 대신 매번 최신 요약을 PR 코멘트로 남깁니다.
- 원상복구하고 자동 업데이트를 다시 켜려면 -> 해당 마커 줄을 삭제하면 됩니다.

## 10. 문제 해결 팁
- Actions가 안 돈다면: 저장소 Settings → Actions에서 권한과 이벤트가 허용되어 있는지 확인
- Auto PR이 베이스를 못 찾는다면: 원격에 `main`이 있는지(`git fetch origin main`) 확인하거나 BASE 값을 수정
- 라벨이 안 붙는다면: `.github/labeler.yml`의 경로 패턴과 실제 변경 파일 경로가 일치하는지 확인

이 문서를 따라 설정하면, "템플릿로 새 저장소를 만든 다음 브랜치에 push" 하는 것만으로 -> "PR 자동 생성/업데이트 + 자동 라벨링 + 표준화된 PR/이슈 작성"이 가능합니다.
