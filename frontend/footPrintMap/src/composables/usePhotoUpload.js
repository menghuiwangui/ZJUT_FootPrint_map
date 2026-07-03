import { ref } from 'vue';

export function usePhotoUpload(formData){
    const fileInput = ref(null);

    function triggerFileInput() {
        fileInput.value.click();
    }

    function handlePhotoUpload(event){
        const MAX_FILE_SIZE = 2 * 1024 * 1024;
        const MAX_COUNT = 9;

        const files = event.target.files;
        if (!files || files.length === 0) return;

        for (const file of files){
            if (file.size > MAX_FILE_SIZE){
                alert(`图片"${file.name}"超过限制大小(2MB)`);
                continue;
            }
            if (formData.value.photos.length >= MAX_COUNT){
                alert ('最多只能上传9张照片');
                continue;
            }
            const reader = new FileReader();
            reader.onload = (e) => {
                formData.value.photos.push(e.target.result);
            };
            reader.readAsDataURL(file);
        }
        event.target.value = '';
    }

    function removePhoto(index){
        formData.value.photos.splice(index, 1);
    }

    return {
        fileInput,
        triggerFileInput,
        handlePhotoUpload,
        removePhoto
    };
    
}