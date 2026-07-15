<script setup>

    import { ref, onMounted } from 'vue'
    import { sendFriendRequestApi, handleFriendRequestApi, getFriendListApi } from '../api/friend' 

    const friendList = ref([])
    const friendRequest = ref({
        friendUserName: '',
        status: '' // 1：同意 2：拒绝 3：删除
    })

    async function handleFriendRequest(){

    }
</script>