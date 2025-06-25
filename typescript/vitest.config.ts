import { defineConfig, type ViteUserConfig } from "vitest/config";

export default defineConfig({
  test: {
    watch: false,
    isolate: true,
    typecheck: {
      include: ["src/**/*.test.ts"],
      enabled: true,
      ignoreSourceErrors: false,
      checker: "tsc",
      tsconfig: "./tsconfig.json",
    },
    include: ["src/**/*.test.ts"],
    silent: true,
  },
}) as ViteUserConfig;
