import { defineStore } from 'pinia';

export const activeTabStore = defineStore({
    id: 'active_tab',
    state: () => ({
        tabName: null,
    }),

    actions: {
        setActive(tab) {
            this.tabName = tab;
        },
        getActive() {
            return this.tabName;
        },
    },
});