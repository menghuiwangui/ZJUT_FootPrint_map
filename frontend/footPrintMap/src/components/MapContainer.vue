<template>
  <div style="position:relative;width:100%;height:100vh;">
    <!-- 地图容器 -->
    <div ref="mapContainer" style="width:100%;height:100%;"></div>

    <!-- ====== 添加足迹弹窗（简约风格） ====== -->
    <div v-if="showForm" class="modal-overlay" @click.self="showForm = false">
      <div class="modal">
        <div class="modal-header">
          <span class="modal-title">✈️ 添加足迹</span>
          <button class="modal-close" @click="showForm = false">×</button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label>📍 地点名称</label>
            <input v-model="formData.name" placeholder="如：西湖" />
          </div>

          <div class="form-group">
            <label>📅 日期</label>
            <input v-model="formData.date" type="date" />
          </div>

          <div class="form-group">
            <label>📝 游记</label>
            <textarea v-model="formData.note" placeholder="写点什么…" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>📸 照片</label>
            <div class="photo-upload-area" @click="triggerFileInput">
              <span>点击上传照片（支持多张）</span>
              <input 
                type="file" 
                accept="image/*" 
                multiple 
                ref="fileInput" 
                @change="handlePhotoUpload" 
                style="display:none;" 
              />
            </div>
            <div v-if="formData.photos && formData.photos.length > 0" class="photo-preview">
              <div 
                v-for="(photo, index) in formData.photos" 
                :key="index" 
                class="photo-item"
              >
                <img :src="photo" alt="预览" />
                <button class="photo-remove" @click="removePhoto(index)">×</button>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label>🏷️ 标签（逗号分隔）</label>
            <input v-model="formData.tags" placeholder="如：美食, 风景" />
          </div>

          <div class="form-coord">
            <span>📍 {{ formData.lat.toFixed(6) }}, {{ formData.lng.toFixed(6) }}</span>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="showForm = false">取消</button>
          <button class="btn-save" @click="handleSaveFootprint">保存足迹</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import AMapLoader from '@amap/amap-jsapi-loader';

import { useFootPrints } from '../composables/useFootPrints';
import { usePhotoUpload } from '../composables/usePhotoUpload';
import { decodedTextSpanIntersectsWith } from 'typescript';

const mapContainer = ref(null);
const markers = ref([]); 
const lightedCities = ref([]);
let map = null; 
let AMapInstance = null;
let geocoder = null;
let districtSearch = null;

const showForm = ref(false);
const formData = ref({
    name: '',
    lat: 0,
    lng: 0,
    date: '',
    note: '',
    tags: '',
    photos: [],
});

const { footprints, addFootPrint: addFootprintData } = useFootPrints();  // 足迹数据管理
const { fileInput, triggerFileInput, handlePhotoUpload, removePhoto } = usePhotoUpload(formData); // 照片上传逻辑

function drawCityBoundary(cityName){
  console.log('开始点亮城市',cityName);

  if (!districtSearch){
    console.log('districtSearch 未初始化');
    return;
  }

  districtSearch.search(cityName, (status, result) => {
    console.log('区划查询 status:', status);
    console.log('区划查询 result:', result);

    if (status === 'complete' && result.districtList.length > 0) {

      const bounds = result.districtList[0].boundaries;
      console.log('获取到的边界数量:', bounds ? bounds.length : 0);

      if (bounds && bounds.length > 0) {
        const polygons = [];
        bounds.forEach(bound => {
          const polygon = new AMapInstance.Polygon({
            path: bound,
            fillColor: '#FF8C00',    // 暖色填充：橘色
            fillOpacity: 0.5,        // 半透明填充
            strokeColor: '#FF4500',  // 边框颜色：橘红
            strokeWeight: 1,
            strokeOpacity: 0.8,
            zIndex: 10,
          });
          polygons.push(polygon);
        });
        map.add(polygons);
        console.log('✅ 城市已点亮:', cityName);
      }
      else{
        console.warn('未获取到该城市的边界数据');
      }
    }
    else{
      console.error('区划查询失败:', status, result);
    }
  });
}

function drawLightedCities(cityName) {
  console.log('开始点亮城市',cityName);

  if (!cityName || lightedCities.value.includes(cityName)){
    console.log('城市已点亮或为空',cityName);
    return;
  }

  lightedCities.value.push(cityName);
  localStorage.setItem('lighted_cities', JSON.stringify(lightedCities.value));

  if (!districtSearch){
    console.log('districtSearch 未初始化');
    return;
  }

  drawCityBoundary(cityName);
}

function handleSaveFootprint(){
  if (formData.value.name === ''){
    alert('请输入足迹名称');
    return;
  }
  
  try{
    const newFootprint = addFootprintData({
      name: formData.value.name,
      lat: formData.value.lat,
      lng: formData.value.lng,
      date: formData.value.date,
      note: formData.value.note || '',
      tags: formData.value.tags,
      photos: formData.value.photos,
    });

    console.log('足迹已保存');

    addMarker(newFootprint);

    if (geocoder){
      const lnglat = [newFootprint.lng, newFootprint.lat];
      geocoder.getAddress(lnglat, (status, result) => {
        console.log('逆地理编码 status:', status);
        console.log('逆地理编码 result:', result);

        if (status === 'complete' && result.regeocode) {
          const city = result.regeocode.addressComponent.city;
          const province = result.regeocode.addressComponent.province; 
          const cityName = (Array.isArray(city) && city.length > 0 ? city[0] : city) || province;
          console.log('解析到的城市:', cityName);
          
          if (cityName) {
            drawLightedCities(cityName);
          } else {
            console.warn('未能解析出城市名');
          }
        } else {
          console.error('逆地理编码失败:', status, result);
        }
      });
    }

    formData.value = {
      name: '',
      lat: 0,
      lng: 0,
      date: '',
      note: '',
      tags: '',
      photos: [],
    }
    showForm.value = false;
  }catch (error){
    alert('保存失败' + error.message);
  }
}

function addMarker(footprint) {
  if (!map || !AMapInstance) {
    console.error('地图或 AMap 未加载');
    return;
  }

  try {
    // 构建弹窗内容（包含照片）
    let photoHtml = '';
    if (footprint.photos && footprint.photos.length > 0) {
      photoHtml = `<div style="display:flex;flex-wrap:wrap;gap:4px;margin:6px 0;">`;
      footprint.photos.forEach(photo => {
        photoHtml += `<img src="${photo}" style="width:70px;height:70px;object-fit:cover;border-radius:6px;" />`;
      });
      photoHtml += `</div>`;
    }

    const popupContent = `
      <div style="max-width:260px;padding:4px 0;">
        <div style="font-weight:700;font-size:16px;color:#1e293b;">${footprint.name}</div>
        <div style="font-size:12px;color:#94a3b8;margin:2px 0 4px;">📅 ${footprint.date}</div>
        ${footprint.note ? `<div style="font-size:13px;color:#475569;margin:4px 0;">${footprint.note}</div>` : ''}
        ${photoHtml}
        <div style="display:flex;flex-wrap:wrap;gap:4px;margin-top:4px;">
          ${footprint.tags.map(t => `<span style="background:#eef2f6;padding:2px 10px;border-radius:20px;font-size:11px;color:#64748b;">#${t}</span>`).join('')}
        </div>
      </div>
    `;

    const marker = new AMapInstance.Marker({
      position: [footprint.lng, footprint.lat],
      title: footprint.name,
      label: {
        content: footprint.name,
        direction: 'top',
        offset: new AMapInstance.Pixel(0, -10),
      },
    });
    marker.setMap(map);

    // 信息窗体
    const infoWindow = new AMapInstance.InfoWindow({
      content: popupContent,
      offset: new AMapInstance.Pixel(0, -30),
    });

    marker.on('click', function() {
      infoWindow.open(map, marker.getPosition());
    });

    // 存储标记引用
    marker._footprintId = footprint.id;
    markers.value.push(marker);

    console.log('✅ 标记已添加:', footprint.name);
  } catch (error) {
    console.error('添加标记失败:', error);
  }
}

// 地图加载 
onMounted(async () => {
  try {
    window._AMapSecurityConfig = {
      securityJsCode: 'cdb6037c48ad95339649133690ad6f15',
    };

    // 1. 加载地图，确保 plugins 里包含 Geocoder 和 DistrictSearch
    const AMap = await AMapLoader.load({
      key: '04889cf2b36a5e208133c70bcc8d33e8', // 换成你自己的 Key
      version: '2.0',
      plugins: ['AMap.Scale', 'AMap.ToolBar', 'AMap.Geocoder', 'AMap.DistrictSearch'],
    });
    AMapInstance = AMap;
    console.log('高德地图 SDK 加载完成');

    // 2. 初始化地图实例
    map = new AMap.Map(mapContainer.value, {
      viewMode: '3D',
      zoom: 5,
      center: [105.602725, 37.076636],
    });
    console.log('地图实例创建完成');

    // 3. 初始化插件 (必须在地图实例创建之后)
    geocoder = new AMapInstance.Geocoder();
    districtSearch = new AMapInstance.DistrictSearch({
      level: 'city',
      subdistrict: 0,
      extensions: 'all',
    });
    console.log('插件初始化完成');

    // 4. 绑定地图点击事件
    map.on('click', function(e){
      console.log('地图被点击了', e);
      const lnglat = e.lnglat || e.lngLat || e.originalEvent?.lngLat;
      if (!lnglat) return;
      
      formData.value.lng = lnglat.lng;
      formData.value.lat = lnglat.lat;
      formData.value.date = new Date().toISOString().slice(0,10);
      showForm.value = true;
    });
    console.log('点击事件绑定成功');

    // 5. 恢复本地存储里已经点亮过的城市
    const storedCities = JSON.parse(localStorage.getItem('lighted_cities') || '[]');
    if (storedCities.length > 0) {
      lightedCities.value = storedCities;
      storedCities.forEach(cityName => {
        drawCityBoundary(cityName);
      });
    }

    // 6. 渲染已有的足迹点
    footprints.value.forEach(f => addMarker(f));

  } catch (error) {
    // 打印出具体的错误，这样如果哪一步失败了，我们能在控制台看到
    console.error('❌ 地图初始化过程中发生错误:', error);
  }
});


// 组件销毁时销毁地图
onUnmounted(() => {
  if (map) {
    map.destroy();
    map = null;
  }
});

</script>

<style scoped>
/* ====== 弹窗样式 ====== */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal {
  background: #ffffff;
  border-radius: 16px;
  width: 100%;
  max-width: 420px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 23px 45px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.2);
  animation: modalIn 0.25s ease;
}

@keyframes modalIn {
  from {
    opacity: 0;
    transform: scale(0.94) translateY(12px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.modal-title {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #94a3b8;
}

.modal-close:hover {
  color: #1e293b;
}

.form-group {
  margin-bottom: 14px;
}

.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 4px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.2s;
  background: #fafbfc;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #4F6EF7;
  box-shadow: 0 0 0 3px rgba(79, 110, 247, 0.12);
}

.form-group textarea {
  resize: vertical;
  min-height: 60px;
}

/* ====== 照片上传 ====== */
.photo-upload-area {
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  cursor: pointer;
  transition: 0.2s;
  background: #fafbfc;
  font-size: 14px;
  color: #94a3b8;
}

.photo-upload-area:hover {
  border-color: #4F6EF7;
  background: #f0f4ff;
}

.photo-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.photo-item {
  position: relative;
  width: 70px;
  height: 70px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #e2e8f0;
  flex-shrink: 0;
}

.photo-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.photo-remove {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: none;
  background: #ef4444;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.photo-remove:hover {
  background: #dc2626;
}

.form-coord {
  font-size: 12px;
  color: #94a3b8;
  background: #f1f4f9;
  padding: 6px 12px;
  border-radius: 6px;
  margin-top: 4px;
  text-align: center;
}

.modal-footer {
  display: flex;
  gap: 10px;
  margin-top: 18px;
  justify-content: flex-end;
}

.modal-footer button {
  padding: 8px 24px;
  border-radius: 30px;
  border: none;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: 0.2s;
}

.btn-cancel {
  background: #f1f4f9;
  color: #475569;
}

.btn-cancel:hover {
  background: #e2e8f0;
}

.btn-save {
  background: #4F6EF7;
  color: #fff;
}

.btn-save:hover {
  background: #3b5de7;
  box-shadow: 0 4px 16px rgba(79, 110, 247, 0.35);
}

/* ====== 地图弹窗样式 ====== */
.custom-popup {
  max-width: 260px;
  padding: 4px 0;
}

.popup-title {
  font-weight: 700;
  font-size: 16px;
  color: #1e293b;
}

.popup-meta {
  font-size: 12px;
  color: #94a3b8;
  margin: 2px 0 4px;
}

.popup-note {
  font-size: 13px;
  color: #475569;
  margin: 4px 0;
}

.popup-photos {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin: 6px 0;
}

.popup-photo {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 6px;
}

.popup-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 4px;
}

.popup-tag {
  background: #eef2f6;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 11px;
  color: #64748b;
}
</style>