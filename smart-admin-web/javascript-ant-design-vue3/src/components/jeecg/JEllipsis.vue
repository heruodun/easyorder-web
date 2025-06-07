<template>
  <a-tooltip placement="topLeft">
    <template v-slot:title>
      <span>{{ value }}</span>
    </template>
    {{ $filters.ellipsis(value, length) }}
  </a-tooltip>
</template>

<script>
export default {
  name: 'JEllipsis',
  props: {
    value: {
      type: String,
      required: false,
    },
    length: {
      type: Number,
      required: false,
      default: 25,
    },
  },
  emits: ['update:value'],
}
</script>

<style scoped></style>
