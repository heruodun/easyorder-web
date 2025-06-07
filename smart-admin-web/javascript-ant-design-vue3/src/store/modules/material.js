/*
 * 商品
 */
import _ from 'lodash';
import { defineStore } from 'pinia';

export const useMaterialStore = defineStore({
  id: 'materialStore',
  state: () => ({
    propertyList: [],
    columns: '',
  }),
  getters: {
    getPropertyList(state) {
      return state.propertyList;
    },

    getColumns(state) {
      return state.columns;
    },
  },

  actions: {
    //超时间时间未设置
    //  // Vue.ls.set('materialPropertyList', res.data.rows, 7 * 24 * 60 * 60 * 1000);
    setPropertyList(propertyList) {
      this.propertyList = propertyList;
    },
    setColumns(columns) {
      this.columns = columns;
    },
    deleteColumns() {
      this.columns = '';
    },
  },
});
