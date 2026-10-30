package com.easyhome.config;
import com.easyhome.model.*;
import com.easyhome.repository.*;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class DemoData {
 @Bean CommandLineRunner createDemo(UserRepository users,ListingRepository listings,PasswordEncoder encoder,@Value("${app.seed-demo}") boolean seed) {
  return args->{
   if(!seed || users.count()>0 || listings.count()>0) return;
   User owner=users.save(new User("Nguyễn Minh Anh","owner@easyhome.vn",encoder.encode("123456"),Role.OWNER));
   users.save(new User("Trần Gia Huy","khach@easyhome.vn",encoder.encode("123456"),Role.RENTER));
   add(listings,owner,"Căn hộ hiện đại gần Hồ Tây","Không gian thoáng, ban công lớn, đầy đủ nội thất. Phù hợp cặp đôi hoặc gia đình nhỏ.","Hà Nội","Tây Hồ","Xuân La, Tây Hồ, Hà Nội","Căn hộ","9500000",68,2,"https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=1100&q=85");
   add(listings,owner,"Phòng studio ngập ánh sáng","Studio khép kín, có điều hòa, máy giặt và chỗ để xe. Khu vực an ninh.","Hà Nội","Cầu Giấy","Dịch Vọng Hậu, Cầu Giấy, Hà Nội","Phòng trọ","3800000",28,1,"https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=1100&q=85");
   add(listings,owner,"Nhà nguyên căn 3 phòng ngủ","Nhà riêng có sân nhỏ, bếp rộng, khu dân cư yên tĩnh, gần trường học.","Hà Nội","Thanh Xuân","Nhân Chính, Thanh Xuân, Hà Nội","Nhà nguyên căn","15000000",110,3,"https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=1100&q=85");
   add(listings,owner,"Căn hộ tiện nghi khu Mỹ Đình","Căn hộ 2 phòng ngủ đầy đủ tiện ích, thuận tiện đi lại và làm việc.","Hà Nội","Nam Từ Liêm","Mỹ Đình 2, Nam Từ Liêm, Hà Nội","Căn hộ","11000000",75,2,"https://images.unsplash.com/photo-1600607687920-4e2a09cf159d?w=1100&q=85");
   add(listings,owner,"Studio trung tâm Quận 1","Nội thất tối giản, không gian sáng và thoáng, thuận tiện sinh hoạt.","TP. Hồ Chí Minh","Quận 1","Đa Kao, Quận 1, TP. Hồ Chí Minh","Phòng trọ","6500000",35,1,"https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?w=1100&q=85");
   add(listings,owner,"Căn hộ ven sông Đà Nẵng","Ban công view thoáng, gần khu trung tâm, dễ di chuyển ra biển.","Đà Nẵng","Sơn Trà","An Hải, Sơn Trà, Đà Nẵng","Căn hộ","8000000",62,2,"https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=1100&q=85");
  };
 }
 private void add(ListingRepository repository,User owner,String title,String desc,String city,String district,String address,String type,String price,double area,int bedrooms,String image) {
  Listing l=new Listing();l.owner=owner;l.title=title;l.description=desc;l.city=city;l.district=district;l.address=address;
  l.type=type;l.price=new BigDecimal(price);l.area=area;l.bedrooms=bedrooms;l.imageUrl=image;repository.save(l);
 }
}
