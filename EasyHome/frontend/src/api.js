export async function api(path,{method='GET',body,token}={}) {
 let res
 try {res=await fetch('/api'+path,{method,headers:{...(body?{'Content-Type':'application/json'}:{}),...(token?{Authorization:'Bearer '+token}:{})},body:body?JSON.stringify(body):undefined})}
 catch {throw new Error('Không kết nối được máy chủ Spring Boot tại cổng 8080. Hãy kiểm tra backend.')}
 let data=null
 const text=await res.text()
 try {data=text?JSON.parse(text):null} catch {data=null}
 if(!res.ok) throw new Error(data?.message || data?.detail || (res.status===401?'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.':'Yêu cầu thất bại ('+res.status+')'))
 return data
}
export const money=n=>Number(n||0).toLocaleString('vi-VN')+' ₫'
export const labels={AVAILABLE:'Đang cho thuê',RENTED:'Đã cho thuê',HIDDEN:'Đã ẩn',PENDING:'Chờ phản hồi',ACCEPTED:'Đã chấp nhận',REJECTED:'Đã từ chối'}
export const types=['Căn hộ','Phòng trọ','Nhà nguyên căn']
export const cities=['Hà Nội','TP. Hồ Chí Minh','Đà Nẵng']
