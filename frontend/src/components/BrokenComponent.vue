<template>
  <div>
    <!-- 렌더링 시 null.property에 접근하여 에러 발생 -->
    <p>{{ causeError() }}</p>
  </div>
</template>

<script setup>
/**
 * BrokenComponent
 * - Vue 렌더링 중 null 참조 에러를 의도적으로 발생시키는 컴포넌트
 * - 이 에러는 Vue의 errorHandler를 통해 Sentry에 캡처됩니다.
 */
function causeError() {
  const obj = null
  // null 객체의 속성에 접근 → TypeError: Cannot read properties of null
  return obj.someProperty
}
</script>
