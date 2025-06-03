import { defineStore } from 'pinia';

export const columnSettingStore = defineStore({
  id: 'column_setting',
  state: () => ({
    columnSetting: null,
  }),

  actions: {
    setColumnSetting(columnSetting) {
      this.columnSetting = columnSetting;
    },
    getColumnSetting() {
      return this.columnSetting;
    },
  },
});
