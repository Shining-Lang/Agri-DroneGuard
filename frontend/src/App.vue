<script setup lang="ts">
const modules = [
  'Task orders',
  'Agent planning',
  'GIS risk validation',
  'Human approval',
  'Simulated dispatch',
]
</script>

<template>
  <main class="page">
    <section class="panel">
      <p class="eyebrow">Agri UAV Agent</p>
      <h1>AI-assisted agricultural UAV planning platform</h1>
      <p class="summary">
        A Java backend plus Python Agent system for route planning, safety approval,
        and simulated UAV task dispatch.
      </p>

      <div class="module-grid">
        <article v-for="module in modules" :key="module" class="module-card">
          {{ module }}
        </article>
      </div>
    </section>
  </main>
</template>
