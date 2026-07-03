import { ref } from 'vue';
const STORAGE_KEY = 'footprints_data';

export function useFootPrints(){
    const footprints = ref(loadFootPrints());

    function loadFootPrints(){
        try{
            const stored = localStorage.getItem(STORAGE_KEY);
            if (stored){
                const data = JSON.parse(stored);
                if (Array.isArray(data) && data.length > 0) return data;
            }
        }catch(error){
            console.error('读取存储失败：', error);
        }
        return [];
    }

    function saveFootPrints(){
        try{
            localStorage.setItem(STORAGE_KEY, JSON.stringify(footprints.value));
        }catch(error){
            console.error('保存存储失败: ', error);
        }
    }

    function addFootPrint(newData){
        const newFootprint = {
            id: Date.now().toString(36) + Math.random().toString(36).slice(2,6),
            name: newData.name,
            lat: newData.lat,
            lng: newData.lng,
            date: newData.date,
            note: newData.note || '',
            tags: newData.tags ? newData.tags.split(',').map(s => s.trim()).filter(Boolean): [],
            photos: newData.photos ? [...newData.photos] : [],
        }
        footprints.value.push(newFootprint);
        saveFootPrints();
        return newFootprint;
    }

    function deleteFootPrint(id){
        footprints.value = footprints.value.filter(f => f.id !== id);
        saveFootPrints();
    }

    return {
        footprints,
        addFootPrint,
        deleteFootPrint,
        saveFootPrints,
    };
}