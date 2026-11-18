# **Software Requirements** 

# **Specification**

**for**

**Dự án Quản lý chung cư** 

**BlueMoon**

**Version 1.0**

 

**Prepared by** 

**Group Name: SE\_02**

| Bùi Anh Tuấn |  | 202417066 | tuan.ba2417066@sis.hust.edu.vn |
| :---- | :---- | ----- | ----- |
| **Tô Văn Thành** |  | **202417034** | **thanh.tv2417034@sis.hust.edu.vn** |
| **Lê Quang Nguyên** |  | **202416994** | **nguyen.lq2416994@sis.hust.edu.vn** |
| **Nguyễn Bá Long** |  | **202416970** | **long.nb2416970@sis.hust.edu.vn** |
|  |  |  |  |
|  |  |  |  |

| Instructor: | TS. Trần Nhật Hóa |
| :---- | :---- |
| **Course:** | **Nhập môn Công nghệ phần mềm** |
| **Date:** | **2025 October 16** |

1. # Introduction

   1. ## 

   2. ## Document Purpose 

Phần mềm được mô tả trong tài liệu này là Hệ thống Quản lý Chung cư (Apartment Management System \- AMS), phiên bản 1.0. Tài liệu SRS này xác định các yêu cầu chức năng và phi chức năng cho hệ thống, bao gồm toàn bộ phạm vi quản lý cư dân, dịch vụ, thanh toán, bảo trì, báo cáo và quản lý vấn đề phát sinh. AMS được phát triển để thay thế quy trình thủ công hiện tại, tập trung vào việc tự động hóa nghiệp vụ, nâng cao hiệu quả và giảm thiểu sai sót. 

3. ## Product Scope

Hệ thống Quản lý Chung cư (AMS) là nền tảng phần mềm tập trung vào việc số hóa quy trình quản lý chung cư, từ quản lý cư dân, thanh toán dịch vụ (điện, nước, phí dịch vụ), đến xử lý vấn đề và phát sinh báo cáo. Hệ thống hướng đến mục tiêu:

* Giảm 80% thời gian xử lý nghiệp vụ thủ công.  
* Tăng độ chính xác trong quản lý tài chính và lưu trữ dữ liệu.  
* Cung cấp giao diện trực quan cho cư dân và ban quản lý.

Lợi ích chính bao gồm: quản lý tập trung, theo dõi thời gian thực, phân quyền truy cập an toàn và khả năng mở rộng cho các tòa nhà mới.

4. ## Intended Audience and Document Overview

Tài liệu này hướng đến:

* Ban quản trị: Xác nhận nghiệp vụ, sử dụng và kiểm soát hệ thống.  
* Nhân viên quản lý: Thực hiện nghiệp vụ hằng ngày theo quyền được cấp.  
* Cư dân: Tra cứu thông tin liên quan, xem thông báo, gửi khiếu nại và thanh toán.  
* Nhà phát triển: Triển khai các chức năng theo yêu cầu.  
* Kiểm thử viên: Xây dựng test case dựa trên yêu cầu.  
* Giảng viên hướng dẫn: Đánh giá tính đầy đủ của SRS.

Cấu trúc tài liệu:

* Giới thiệu: mục đích, phạm vi và các tài liệu liên quan.  
* Mô tả tổng quan: tổng quan về hệ thống.  
* Đặc tả yêu cầu:  yêu cầu về chức năng, use case, luồng nghiệp vụ.  
* Yêu cầu phi chức năng: hiệu năng, bảo mật và chất lượng phần mềm.

  5. ## Definitions, Acronyms and Abbreviations

| Thuật ngữ | Định nghĩa |
| :---- | :---- |
| Ban quản trị (BQT) | Vai trò có quyền cao nhất trong phạm vi nghiệp vụ của hệ thống , thực hiện các chức năng quản lý chung cư và quản lý tài khoản theo quyền được quy định.  |
| Nhân viên quản lý (NV) | Người thực hiện các nghiệp vụ quản lý theo quyền được cấp. |
| Cư dân | Người sinh sống tại chung cư và sử dụng các chức năng dành cho cư dân. |
| Tài khoản | Thông tin định danh cho phép người dùng đăng nhập hệ thống. |
| Vai trò | Nhóm quyền xác định các chức năng người dùng được phép thực hiện. |
| Phân quyền | Việc xác định quyền truy cập và thực hiện chức năng của người dùng. |
| Căn hộ | Đơn vị nhà ở được quản lý trong hệ thống, có mã căn hộ, diện tích và trạng thái sử dụng. |
| Hộ gia đình | Nhóm cư dân cùng sinh sống trong một căn hộ. |
| Biến động cư trú | Sự thay đổi tình trạng cư trú như chuyển đến, chuyển đi, tạm trú hoặc tạm vắng. |
| Khoản thu | Khoản tiền được quản lý trên hệ thống để thu phí hoặc tiếp nhận đóng góp. |
| Khoản thu bắt buộc | Khoản tiền căn hộ có trách nhiệm nộp theo quy định của chung cư. |
| Khoản đóng góp tự nguyện | Khoản tiền cư dân có thể tự nguyện tham gia đóng góp. |
| Kỳ thu | Khoảng thời gian áp dụng cho phí dịch vụ hoặc phí quản lý. |
| Đợt đóng góp | Đợt tổ chức tiếp nhận khoản đóng góp tự nguyện. |
| Nghĩa vụ đóng phí | Khoản tiền một căn hộ có trách nhiệm nộp đối với khoản thu bắt buộc. |
| Phí dịch vụ | Khoản phí bắt buộc được tính dựa trên diện tích căn hộ và đơn giá tương ứng. |
| Phí quản lý | Khoản phí bắt buộc phục vụ công tác quản lý chung cư, được tính theo diện tích và đơn giá tương ứng. |
| Đơn giá tính phí | Mức tiền áp dụng cho một đơn vị tính phí, chẳng hạn đồng/m²/tháng. |
| Hạn nộp | Thời điểm cuối cùng căn hộ cần hoàn thành việc nộp tiền. |
| Số tiền phải thu | Giá trị tiền cần thu đối với một nghĩa vụ đóng phí bắt buộc. |
| Ghi nhận nộp tiền | Nghiệp vụ cập nhật thông tin về khoản tiền đã được nộp vào hệ thống. |
| Bản ghi nộp tiền | Bản ghi lưu thông tin về một lần nộp tiền đã được ghi nhận. |
| Trạng thái nộp tiền | Trạng thái thể hiện tình hình nộp tiền của căn hộ đối với khoản thu bắt buộc. |
| Hủy khoản thu | Thao tác chuyển khoản thu sang trạng thái đã hủy theo quyền được cấp. |
| Hủy bản ghi nộp tiền | Thao tác hủy hiệu lực nghiệp vụ của bản ghi nộp tiền theo quyền được cấp. |
| Tổng phải thu | Tổng giá trị các khoản thu bắt buộc được tính trong phạm vi thống kê, không gồm khoản thu đã hủy. |
| Tổng đã thu | Tổng số tiền từ các khoản thu bắt buộc đã được ghi nhận thành công. Tổng đóng góp tự nguyện được thống kê riêng.  |
| Còn phải thu | Phần tiền của các nghĩa vụ đóng phí bắt buộc chưa được nộp. |
| Thông báo | Nội dung do BQT hoặc NV tạo để gửi đến cư dân. |
| Khiếu nại | Phản ánh do cư dân gửi để yêu cầu BQT hoặc NV xem xét, xử lý. |
| Trạng thái khiếu nại | Trạng thái thể hiện tiến trình xử lý khiếu nại, gồm mới, đang xử lý và đã giải quyết. |
| RBAC | Role-Based Access Control \- cơ chế phân quyền dựa trên vai trò. |
| AMS | Apartment Management System – Hệ thống Quản lý Chung cư |
| API | Application Programming Interface |
| SRS | Software Requirements Specification |
| UI | User Interface – Giao diện người dùng |

  6. ## Document Conventions

**Tài liệu tuân thủ chuẩn IEEE 830-1998 cho SRS:**

* Font chữ: Arial cỡ 11 cho nội dung, Arial 14 (bold) cho tiêu đề.  
* Định dạng: Lề 1 inch, giãn cách dòng đơn.

**Quy ước:**

* Chữ nghiêng: Ghi chú hoặc ví dụ.  
* In đậm: Thuật ngữ quan trọng.  
* Mã code/API: Đặt trong khối code.  
* Tên tệp, thư mục: Viết hoa chữ cái đầu (VD: PaymentModule).

  7. ## References and Acknowledgments

**Tài liệu tham khảo:**

* Tài liệu hướng dẫn bài tập lớn  
* IEEE SRS Template (1998).  
* Hướng dẫn UML 2.0 – OMG.  
* **SePay Developer – SePay Webhooks:** [https\://developer.sepay.vn/vi/sepay-webhooks](https://developer.sepay.vn/vi/sepay-webhooks).

* **SePay Developer – Webhook IPN biến động số dư:** [https\://developer.sepay.vn/vi/bankhub/thong-bao-bien-dong-so-du](https://developer.sepay.vn/vi/bankhub/thong-bao-bien-dong-so-du).

2. # Overall Description

   1. ## Product Overview

Hệ thống Quản lý Thu phí Chung cư là một nền tảng web được xây dựng nhằm hỗ trợ Ban quản lý trong việc tổ chức, quản lý và theo dõi các hoạt động thu phí tại chung cư. Hệ thống tập trung vào việc quản lý các khoản thu, ghi nhận thanh toán, theo dõi tình trạng đóng phí và tổng hợp số liệu thu phí. Bên cạnh đó, hệ thống lưu trữ và quản lý thông tin cư dân, hộ gia đình và căn hộ làm cơ sở xác định đối tượng thu, tính toán các khoản phí và theo dõi nghĩa vụ thanh toán. Qua đó, hệ thống góp phần thay thế phương pháp quản lý thủ công, nâng cao hiệu quả xử lý, độ chính xác và tính minh bạch trong công tác thu phí chung cư.

Hệ thống bao gồm các thành phần chính:

* **Frontend:** Sử dụng ReactJS để xây dựng giao diện người dùng trực quan, dễ sử dụng.  
* **Backend:** Được phát triển bằng Spring Boot, cung cấp API RESTful để xử lý dữ liệu và logic nghiệp vụ.  
* **Cơ sở dữ liệu:** MySQL để lưu trữ thông tin cư dân, tài sản và dịch vụ.  
* **Xác thực và bảo mật:** JWT được sử dụng để xác thực người dùng, đảm bảo an toàn dữ liệu.

Hệ thống quản lý chung cư là một phần mềm độc lập nhưng có khả năng tích hợp với các hệ thống bên ngoài như:

* Cư dân chuyển khoản bằng mã QR và tự động đối soát giao dịch qua SePay webhook.  
* Cơ sở dữ liệu lưu trữ thông tin nhân khẩu, tài chính, và lịch sử giao dịch.

**Sơ đồ tổng quan hệ thống:** 

![][image1]

2. ## Product Perspective

   1. ### System Interfaces

Thanh toán bằng mã QR và đối soát giao dịch: Hệ thống sử dụng mã QR chuyển khoản ngân hàng và tích hợp SePay webhook để tự động nhận biến động số dư, đối soát giao dịch và cập nhật trạng thái thanh toán khi giao dịch khớp. 

Dịch vụ thông báo: Hệ thống hiển thị thông báo trực tiếp trong ứng dụng BlueMoon. 

2. ### User Interfaces

Web-based dashboard với các thành phần:

* Giao diện đăng nhập và quản lý tài khoản theo ba vai trò: Ban quản trị (BQT), Nhân viên (NV) và Cư dân.  
* Giao diện quản lý căn hộ, cư dân, khoản thu, thanh toán và thống kê.  
* Giao diện cư dân để xem thông tin căn hộ, các khoản phí, lịch sử thanh toán và thông báo.  
* Giao diện tạo và quản lý thông báo, tiếp nhận và xử lý khiếu nại.  
* Giao diện thanh toán bằng mã QR và theo dõi trạng thái thanh toán.

Tối ưu hóa:

* Giao diện thân thiện, dễ sử dụng và nhất quán giữa các chức năng.  
* Thiết kế responsive, phù hợp với máy tính, máy tính bảng và điện thoại.  
* Hiển thị rõ trạng thái khoản thu và thanh toán, hạn chế thao tác nhầm.  
* Phân quyền giao diện theo vai trò người dùng.

  3. ### Hardware Interfaces

Máy chủ:

* Hệ thống được triển khai trên máy chủ hoặc nền tảng điện toán đám mây có khả năng chạy ứng dụng web backend và kết nối cơ sở dữ liệu MySQL.  
* Máy chủ cần có kết nối Internet để phục vụ truy cập hệ thống và tiếp nhận webhook từ SePay.

Thiết bị đầu cuối:

* Máy tính, máy tính bảng hoặc điện thoại có trình duyệt web hiện đại và kết nối Internet.  
* Thiết bị có khả năng quét mã QR bằng camera hoặc ứng dụng ngân hàng để thực hiện chuyển khoản.

  4. ### Software Interface

| Thành phần | Yêu cầu |
| ----- | ----- |
| Frontend | ReactJS. |
| Backend | Java Spring Boot. |
| Cơ sở dữ liệu | MySQL Server/CSDL MySQL tập trung. |
| Trình duyệt | Trình duyệt hiện đại có hỗ trợ ứng dụng web; danh sách phiên bản cụ thể được xác định trong giai đoạn thiết kế/kiểm thử. |
| Ứng dụng ngân hàng | Cư dân dùng ứng dụng ngân hàng của mình để chuyển khoản; thao tác chuyển tiền diễn ra ngoài BlueMoon. |
| SePay | Dịch vụ bên ngoài gửi thông báo giao dịch/biến động số dư tới một endpoint webhook của backend. Payload, phương thức xác thực và phản hồi phải theo đúng sản phẩm SePay mà nhóm đăng ký sử dụng. |

     5. ### Communications Interfaces

Giao thức:

* HTTPS để trao đổi dữ liệu an toàn giữa trình duyệt và hệ thống.  
* REST API qua HTTP/HTTPS để giao tiếp giữa frontend ReactJS và backend Spring Boot.  
* Webhook HTTPS để tiếp nhận biến động số dư từ SePay.

Port mặc định:

* HTTP: 80; HTTPS: 443 khi triển khai web.  
* Backend Spring Boot: sử dụng cổng được cấu hình trong môi trường triển khai.  
* MySQL: 3306 theo cấu hình mặc định; chỉ cho phép truy cập từ các thành phần hệ thống được cấp quyền.

  6. ### Memory Constraints

Dung lượng lưu trữ: 

* Lưu trữ dữ liệu căn hộ, cư dân, khoản thu, thanh toán, thông báo, khiếu nại và lịch sử giao dịch trong cơ sở dữ liệu MySQL.  
* Dung lượng cụ thể được xác định theo quy mô dữ liệu thực tế và môi trường triển khai; chưa quy định con số cố định trong SRS.

RAM: 

* Máy chủ cần có đủ bộ nhớ để vận hành backend, cơ sở dữ liệu và xử lý các yêu cầu đồng thời.  
* Dung lượng RAM tối thiểu chưa được xác định; cần lựa chọn theo môi trường triển khai và kết quả kiểm thử hiệu năng.

  7. ### Operations

Chế độ thường: 

* Người dùng đăng nhập và sử dụng chức năng theo vai trò, quyền hạn được cấp.  
* Hệ thống xử lý các nghiệp vụ quản lý căn hộ, cư dân, khoản thu, thanh toán, thống kê, thông báo và khiếu nại.  
* Hệ thống tiếp nhận webhook từ SePay và tự động đối soát các giao dịch đáp ứng điều kiện hợp lệ.

Chế độ khẩn cấp:

* Khi xảy ra lỗi hệ thống hoặc mất kết nối với dịch vụ bên ngoài, hệ thống thông báo lỗi phù hợp và không tự động xác nhận các giao dịch chưa được xác minh.  
* Giao dịch chưa đối soát được chuyển sang trạng thái cần kiểm tra để BQT/NV xử lý khi hệ thống hoạt động trở lại.  
* Cần có cơ chế sao lưu và khôi phục dữ liệu để hạn chế mất mát thông tin khi xảy ra sự cố.

  3. ## Product Functionality 

Hệ thống cung cấp các chức năng chính sau:

| Mã nhóm | Nhóm chức năng | Chức năng chính | Vai trò |
| ----- | ----- | ----- | ----- |
| F01 | Quản lý tài khoản và phân quyền | Đăng nhập/đăng xuất, đổi mật khẩu, quản lý hồ sơ cá nhân, tạo và quản lý tài khoản theo quyền được cấp, đặt lại mật khẩu và kiểm soát quyền truy cập | BQT, NV và Cư dân. BQT quản lý tài khoản NV và BQT; BQT/NV tạo tài khoản Cư dân theo quyền được cấp. Người dùng không tự đăng ký tài khoản. |
| F02 | Quản lý căn hộ | Thêm, sửa, xem, tra cứu và quản lý trạng thái căn hộ | BQT, NV; cư dân xem căn hộ liên quan |
| F03 | Quản lý cư dân | Quản lý hồ sơ cư dân và hộ gia đình; liên kết hộ gia đình với căn hộ theo thời gian; tra cứu thông tin cư trú và lịch sử liên quan | BQT, NV; cư dân xem dữ liệu của mình và thông tin căn hộ liên quan theo quyền được cấp |
| F04 | Quản lý biến động cư trú | Ghi nhận chuyển đến, chuyển đi, tạm trú và tạm vắng; cập nhật quan hệ giữa hộ gia đình, cư dân và căn hộ; lưu lịch sử cư trú, không ghi đè lịch sử đã phát sinh | BQT, NV |
| F05 | Quản lý khoản thu và đóng góp | Tạo và cập nhật khoản thu bắt buộc theo quyền; xác định kỳ thu, hạn nộp và đối tượng áp dụng; tính phí theo diện tích và đơn giá; lưu diện tích, đơn giá và số tiền tại thời điểm phát sinh; ngăn tạo trùng nghĩa vụ theo căn hộ, loại khoản thu và kỳ thu; quản lý đợt đóng góp tự nguyện. Chỉ BQT được hủy khoản thu | BQT, NV; cư dân xem khoản thu liên quan và thông tin đợt đóng góp |
| F06 | Quản lý thu tiền và thanh toán QR  | Ghi nhận tiền mặt/chuyển khoản thông thường sau khi xác minh; tạo thông tin và mã QR thanh toán khoản thu bắt buộc; tiếp nhận và đối soát giao dịch qua SePay; ghi nhận số tiền đóng góp thực tế; xử lý giao dịch cần kiểm tra, ngăn ghi nhận trùng; tra cứu lịch sử và hủy bản ghi nộp tiền theo quyền. Chỉ BQT được hủy bản ghi nộp tiền | BQT, NV; cư dân thanh toán/đóng góp và xem trạng thái giao dịch của mình  |
| F07 | Tra cứu và thống kê | Tra cứu khoản thu, trạng thái nộp tiền và lịch sử; thống kê tổng phải thu, tổng đã thu, còn phải thu và tổng đóng góp. Công nợ được theo dõi theo từng hộ gia đình, không chuyển sang hộ mới chỉ vì hộ đó tiếp nhận cùng căn hộ | BQT, NV; cư dân xem dữ liệu của mình |
| F08 | Quản lý thông báo | Tạo thông báo, chọn người nhận, xem thông báo, cấu hình lịch nhắc phí và tự động gửi thông báo theo lịch; BQT được xóa thông báo | BQT, NV, Cư dân |
| F09 | Quản lý khiếu nại | Cư dân gửi/theo dõi; BQT/NV cập nhật trạng thái và nhập phản hồi | BQT, NV, Cư dân |

4. ## User Characteristics

   ### **Ban quản trị (BQT)**

* Giám sát và quản lý các nghiệp vụ thuộc phạm vi hệ thống.  
* Quản lý tài khoản BQT và NV theo quyền được cấp; tạo và đặt lại mật khẩu tài khoản cư dân theo quy định của hệ thống.  
* Quản lý đơn giá phí dịch vụ/phí quản lý.  
* Có quyền hủy khoản thu, hủy bản ghi nộp tiền và xóa thông báo.  
* Theo dõi kết quả đối soát tự động; kiểm tra và xử lý các giao dịch QR không khớp, thiếu dữ liệu hoặc cần xác minh bổ sung.  
* Cấu hình lịch nhắc phí tự động theo các chức năng được hệ thống hỗ trợ.

  ### **Nhân viên quản lý (NV)**

* Quản lý căn hộ, cư dân, biến động cư trú, khoản thu, ghi nhận nộp tiền, tra cứu và thống kê theo quyền được cấp.  
* Có thể tạo và sửa khoản thu nhưng không được hủy khoản thu.  
* Có thể ghi nhận nộp tiền mặt/chuyển khoản thông thường nhưng không được hủy bản ghi nộp tiền.  
* Theo dõi kết quả đối soát QR và kiểm tra, xử lý các trường hợp ngoại lệ trong phạm vi quyền được cấp; không cần xác nhận thủ công từng giao dịch đã được hệ thống đối soát thành công.  
* Có thể tạo thông báo nhưng không được xóa thông báo; có thể tiếp nhận, cập nhật trạng thái xử lý và phản hồi khiếu nại theo quyền được cấp.  
* Không được quản lý tài khoản NV hoặc thực hiện các thao tác dành riêng cho BQT.

  ### **Cư dân**

* Sử dụng tài khoản do BQT/NV cấp; không được tự đăng ký tài khoản.  
* Xem thông tin cá nhân, căn hộ hiện tại, các khoản thu và tình trạng nộp tiền liên quan đến hộ gia đình của mình theo quyền được cấp.  
* Chọn khoản thu cần nộp, xem mã QR và thực hiện chuyển tiền qua ứng dụng ngân hàng. Hệ thống tự cập nhật trạng thái nộp tiền sau khi nhận và đối soát thành công thông tin giao dịch từ SePay.  
* Thực hiện đóng góp tự nguyện qua QR theo đợt đóng góp; người đóng tự nhập số tiền muốn đóng. Hệ thống ghi nhận khoản đóng góp sau khi giao dịch được xác minh theo quy trình đối soát.  
* Xem các thông báo được gửi đến mình.  
* Gửi và theo dõi khiếu nại của chính mình; không được tự cập nhật trạng thái xử lý hoặc phản hồi khiếu nại.  
* Không được xem dữ liệu của cư dân/hộ gia đình khác hoặc tự tạo, sửa, xác nhận hay hủy bản ghi nộp tiền.

  5. ## Design and Implementation Constraints

Hệ thống được phát triển với các ràng buộc sau:

* Pháp lý: Tuân thủ các quy định pháp luật Việt Nam có liên quan đến bảo vệ dữ liệu cá nhân và bảo mật thông tin cư dân, tài chính, lịch sử giao dịch.  
* Ngôn ngữ lập trình:Java cho backend; JavaScript/TypeScript và ReactJS cho frontend theo kiến trúc triển khai của nhóm.  
* Cơ sở dữ liệu: MySQL để lưu trữ thông tin căn hộ, cư dân, khoản thu, thanh toán và các dữ liệu nghiệp vụ liên quan.  
* Bảo mật: Xác thực người dùng, phân quyền theo ba vai trò BQT, NV và Cư dân; bảo vệ dữ liệu và xác minh tính hợp lệ của webhook trước khi xử lý giao dịch.  
* Giao thức truyền thông: HTTPS để truyền dữ liệu giữa trình duyệt và hệ thống; REST API để giao tiếp giữa frontend và backend; webhook HTTPS để tiếp nhận biến động số dư từ SePay.  
* Yêu cầu hiệu suất: Hệ thống cần đáp ứng thời gian phản hồi và khả năng xử lý theo các mục tiêu hiệu năng được xác định trong SRS; bảo đảm việc xử lý giao dịch không tạo bản ghi thanh toán trùng lặp.  
* Chuẩn thiết kế: Áp dụng kiến trúc MVC và mô hình phân lớp Controller – Service – Repository ở backend Spring Boot; frontend được tổ chức thành các thành phần giao diện có thể tái sử dụng.

  6. ## Assumptions and Dependencies

**Assumptions (Giả định):**

* Người dùng có thiết bị kết nối Internet và trình duyệt web để truy cập hệ thống BlueMoon.  
* Mỗi người dùng được cấp tài khoản và phân quyền theo một trong ba vai trò: Ban quản trị (BQT), Nhân viên (NV) hoặc Cư dân.  
* Thông tin căn hộ, hộ gia đình, cư dân, các khoản phí và giao dịch thanh toán do người có thẩm quyền cung cấp hoặc cập nhật phải chính xác.  
* Cư dân sử dụng mã QR để chuyển khoản ngân hàng theo hướng dẫn của hệ thống khi thanh toán phí.

**Dependencies (Các phụ thuộc):**

* Hệ thống phụ thuộc vào cơ sở dữ liệu MySQL để lưu trữ và truy xuất thông tin.  
* Hệ thống phụ thuộc vào kết nối Internet để người dùng truy cập và trao đổi dữ liệu với máy chủ.  
* Chức năng tự động đối soát thanh toán phụ thuộc vào dịch vụ SePay webhook và việc tiếp nhận được thông tin biến động giao dịch ngân hàng.  
* Việc xác định trạng thái thanh toán tự động phụ thuộc vào khả năng đối chiếu chính xác thông tin giao dịch nhận được với khoản phí cần thanh toán. Các giao dịch không khớp cần được kiểm tra thủ công.

3. # Specific Requirements

   1. ## External Interface Requirements

      1. ### User Interfaces

**Trang Đăng nhập:** 

Hệ thống cung cấp trang đăng nhập để người dùng truy cập bằng tài khoản được cấp. Sau khi đăng nhập thành công, hệ thống xác định vai trò của người dùng và cho phép truy cập các chức năng tương ứng. Hệ thống không hỗ trợ người dùng tự đăng ký tài khoản.

**Bảng điều khiển (Dashboard):**

Bảng điều khiển hiển thị các chức năng phù hợp với vai trò của người dùng:

* **Ban quản trị (BQT):** Quản lý tài khoản, căn hộ, hộ gia đình, cư dân, các khoản phí, thanh toán, thống kê và các chức năng quản lý khác thuộc phạm vi hệ thống.  
* **Nhân viên (NV):** Thực hiện các nghiệp vụ quản lý được phân quyền, như cập nhật thông tin liên quan, theo dõi phí và ghi nhận thanh toán.  
* **Cư dân:** Xem thông tin căn hộ của mình, các khoản phí cần thanh toán, lịch sử thanh toán và thông báo; thực hiện thanh toán theo hướng dẫn của hệ thống.

Giao diện cần trình bày thông tin rõ ràng, dễ sử dụng và hạn chế hiển thị các chức năng không thuộc quyền truy cập của người dùng.

2. ### System Interface:

**Hardware Interfaces:**

Hệ thống hoạt động trên máy chủ triển khai ứng dụng và cơ sở dữ liệu. Người dùng truy cập hệ thống bằng máy tính, máy tính bảng hoặc điện thoại có trình duyệt web và kết nối Internet. Để thanh toán bằng mã QR, người dùng cần thiết bị hoặc ứng dụng ngân hàng có khả năng quét mã QR và thực hiện chuyển khoản.

**Software Interfaces:**

* **Frontend:** ReactJS, cung cấp giao diện web và tương tác với người dùng.  
* **Backend:** Java Spring Boot, xử lý nghiệp vụ, xác thực và phân quyền người dùng, đồng thời cung cấp API cho frontend.  
* **Cơ sở dữ liệu:** MySQL, lưu trữ thông tin tài khoản, căn hộ, hộ gia đình, cư dân, khoản phí, thanh toán và các dữ liệu nghiệp vụ liên quan.  
* **Dịch vụ đối soát thanh toán:** SePay webhook, tiếp nhận thông tin biến động giao dịch ngân hàng để đối soát với khoản phí trong hệ thống và cập nhật trạng thái thanh toán khi giao dịch hợp lệ, khớp thông tin.

  2. ## Functional Requirements

     1. ### Xác thực tài khoản

* Cư dân không được tự đăng ký tài khoản; tài khoản cư dân do BQT/NV tạo theo quyền được cấp.  
* Hệ thống phải từ chối đăng nhập nếu thông tin không đầy đủ, không chính xác hoặc tài khoản không được phép sử dụng.  
* Sau khi đăng nhập thành công, hệ thống xác định vai trò BQT, NV hoặc Cư dân và chuyển người dùng đến giao diện phù hợp.  
* Người dùng có thể đăng xuất và đổi mật khẩu sau khi xác minh mật khẩu hiện tại.  
* Hệ thống phải kiểm tra quyền truy cập ở phía backend đối với từng chức năng và dữ liệu.  
* Cư dân không được tự đăng ký tài khoản; tài khoản cư dân do BQT/NV tạo theo quyền được cấp.

  2. ### Quản lý tài khoản

* BQT có quyền tạo và quản lý tài khoản BQT khác theo quy định phân quyền của hệ thống.  
* BQT có quyền tạo và quản lý tài khoản nhân viên.  
* BQT/NV có quyền tạo tài khoản cư dân và đặt lại mật khẩu cư dân theo quyền được cấp.  
* NV không được tạo, sửa, khóa hoặc xóa tài khoản BQT và tài khoản NV.  
* Người dùng không được tự đăng ký tài khoản. Khi quên mật khẩu, người dùng có thể yêu cầu đặt lại mật khẩu bằng OTP gửi đến email đã đăng ký.  
* Tài khoản BQT đầu tiên được nhóm phát triển khởi tạo khi triển khai hệ thống.  
* Hệ thống phải liên kết tài khoản cư dân với hồ sơ cư dân tương ứng, đồng thời giới hạn quyền truy cập theo quan hệ cư trú hiện tại. Khi cư dân chuyển đi, hệ thống phải giữ lịch sử liên kết và không cho phép cư dân tiếp tục truy cập dữ liệu không còn thuộc phạm vi quyền của mình.  
* Người dùng chỉ được cập nhật thông tin cá nhân trong phạm vi quyền được cấp.

  3. ### Quản lý căn hộ

* BQT/NV có thể tạo, xem, tra cứu và cập nhật thông tin căn hộ theo quyền được cấp.  
* Thông tin căn hộ tối thiểu gồm mã căn hộ, diện tích và trạng thái sử dụng.  
* Hệ thống phải kiểm tra mã căn hộ không bị trùng.  
* Không cho phép xóa vật lý căn hộ đã được tạo; căn hộ không còn sử dụng phải được chuyển sang trạng thái “Ngừng sử dụng”.  
* Hệ thống phải phân biệt trạng thái hoạt động của căn hộ với tình trạng có hoặc không có cư dân.  
* Cư dân chỉ được xem thông tin căn hộ liên quan đến mình theo quyền được cấp.  
* Hệ thống phải lưu lịch sử liên kết giữa hộ gia đình và căn hộ, bao gồm thời điểm bắt đầu và kết thúc. Khi hộ gia đình chuyển đi và hộ mới chuyển đến cùng căn hộ, hệ thống phải giữ nguyên lịch sử của hộ cũ và không chuyển công nợ của hộ cũ sang hộ mới.

  4. ### Quản lý cư dân và biến động cư trú

* BQT/NV có thể tạo, xem, sửa và tra cứu hồ sơ cư dân theo quyền.  
* Hệ thống hỗ trợ ghi nhận các biến động cư trú gồm chuyển đến, chuyển đi, tạm trú và tạm vắng.  
* Hệ thống phải lưu lịch sử biến động cư trú, bao gồm hộ gia đình, căn hộ và thời gian cư trú tương ứng, để phục vụ tra cứu.  
* Hồ sơ cư dân phải được liên kết với hộ gia đình và căn hộ cư trú tương ứng.  
* Hộ gia đình là đối tượng xác định nghĩa vụ phí và theo dõi công nợ. Khi hộ chuyển đi, khoản thu và công nợ chưa hoàn thành vẫn thuộc hộ cũ; hộ mới không phải chịu khoản nợ của hộ cũ.  
* Cư dân chỉ được xem thông tin cá nhân và dữ liệu căn hộ, hộ gia đình liên quan đến mình theo quyền được cấp.

  5. ### Quản lý khoản thu và đóng góp

* BQT/NV có thể tạo khoản thu bắt buộc theo loại khoản thu, kỳ thu, hạn nộp, mô tả và phạm vi áp dụng. Nghĩa vụ phí được gắn với hộ gia đình tương ứng tại thời điểm phát sinh.  
* Đơn giá mặc định là 2.500 VNĐ/m²/tháng đối với phí dịch vụ và 7.000 VNĐ/m²/tháng đối với phí quản lý. BQT có quyền thay đổi đơn giá.  
* Phí dịch vụ/phí quản lý được tính bằng diện tích căn hộ nhân với đơn giá áp dụng. Hệ thống lưu diện tích, đơn giá và số tiền tại thời điểm tạo khoản thu.  
* Người có quyền có thể sửa hạn nộp và mô tả; mục tiêu đợt đóng góp tự nguyện cũng có thể được sửa. Không cho phép thay đổi trực tiếp loại khoản thu, kỳ thu, diện tích áp dụng, đơn giá đã chốt hoặc số tiền đã tính.  
* Hệ thống ngăn tạo trùng nghĩa vụ phí còn hiệu lực cho cùng hộ gia đình, loại khoản thu và kỳ thu. Nghĩa vụ và công nợ tiếp tục thuộc hộ gia đình phát sinh, không chuyển sang hộ mới chỉ vì cùng căn hộ.   
* Chỉ BQT được hủy khoản thu. Bản ghi đã hủy vẫn được lưu lịch sử và không tính vào tổng phải thu.  
* BQT/NV có thể tạo đợt đóng góp tự nguyện và ghi nhận số tiền thực tế. Hệ thống hỗ trợ QR đóng góp riêng với QR nộp phí bắt buộc; người đóng tự nhập số tiền. Không coi hộ không đóng góp là đang nợ.

  6. ### Quản lý thu tiền và thanh toán 

* BQT/NV có thể ghi nhận tiền mặt hoặc chuyển khoản thông thường sau khi kiểm tra giao dịch.  
* Cư dân có thể chọn khoản thu của hộ mình và xem QR nộp phí bắt buộc, gồm tài khoản nhận, số tiền còn phải nộp và mã tham chiếu. QR đóng góp tự nguyện được quản lý riêng theo từng đợt đóng góp.  
* Hệ thống tích hợp SePay webhook để tiếp nhận thông tin giao dịch ngân hàng, xác thực dữ liệu và đối chiếu mã tham chiếu, tài khoản nhận, số tiền cùng các thông tin cần thiết.  
* Chỉ giao dịch hợp lệ, xác định duy nhất được khoản thu hoặc đợt đóng góp mới được tự động ghi nhận. Hệ thống phải ngăn xử lý trùng giao dịch, kể cả khi webhook được gửi lại.  
* Giao dịch thiếu dữ liệu, không khớp hoặc không xác định được đối tượng phải được chuyển sang trạng thái “Cần kiểm tra”. BQT/NV xử lý ngoại lệ theo quyền được cấp; không cần xác nhận thủ công từng giao dịch đã đối soát thành công.  
* Phạm vi hiện tại không hỗ trợ thanh toán một phần đối với khoản phí bắt buộc.  
* Chỉ BQT được hủy bản ghi nộp tiền. Bản ghi bị hủy vẫn được lưu lịch sử nhưng không tính vào tổng đã thu. Cư dân không được tự tạo, sửa, xác nhận hoặc hủy bản ghi nộp tiền.  
  7. Tra cứu và thống kê

* BQT/NV có thể tra cứu khoản thu theo loại, kỳ thu, căn hộ và trạng thái; tra cứu lịch sử nộp tiền theo hộ gia đình, căn hộ, khoản thu và thời gian.  
* Hệ thống thống kê tổng tiền phải thu, tổng tiền đã thu, số tiền còn phải thu và tình hình đóng góp tự nguyện.  
* Khi thống kê, hệ thống loại trừ khoản thu đã hủy khỏi tổng phải thu và bản ghi nộp tiền đã hủy khỏi tổng đã thu.  
* Khoản đóng góp tự nguyện được thống kê theo số tiền thực tế đã ghi nhận; không tạo công nợ cho hộ gia đình không tham gia đóng góp.  
* Hệ thống phải phân biệt dữ liệu phí bắt buộc và đóng góp tự nguyện, tránh cộng gộp sai mục đích thống kê.  
* Cư dân chỉ được xem thông tin khoản thu, nộp tiền và công nợ liên quan đến hộ gia đình của mình; không được xem thống kê toàn chung cư.  
* Báo cáo hiển thị trong ứng dụng; chức năng xuất Excel/PDF không thuộc phạm vi hiện tại.

  8. Quản lý thông báo

* BQT/NV có thể tạo thông báo gồm tiêu đề, nội dung và đối tượng nhận; đối tượng nhận có thể là tất cả cư dân hoặc cư dân thuộc các căn hộ được chọn.  
* Hệ thống hiển thị thông báo trong ứng dụng và hỗ trợ cấu hình lịch nhắc phí tự động.  
* Cư dân chỉ được xem thông báo gửi đến mình.  
* BQT được quyền xóa thông báo; NV không có quyền xóa.  
* Gửi thông báo qua email, SMS hoặc push notification không thuộc phạm vi hiện tại.  
  9. Quản lý khiếu nại và phản hồi

* Cư dân có thể gửi khiếu nại gồm tiêu đề và nội dung.  
* BQT/NV có thể xem và xử lý khiếu nại theo quyền được cấp; trạng thái gồm “Mới”, “Đang xử lý” và “Đã giải quyết”.  
* BQT/NV có thể nhập nội dung phản hồi cho khiếu nại.  
* Cư dân chỉ được xem khiếu nại do mình gửi và nội dung phản hồi tương ứng.  
* Phạm vi hiện tại không hỗ trợ chat thời gian thực, hội thoại nhiều lượt hoặc tệp đính kèm cho khiếu nại.

  3. ## Performance Required

Các yêu cầu về hiệu năng của hệ thống được quy định chi tiết tại mục 4.1 – Performance Requirements. Các chỉ tiêu thời gian phản hồi phải được kiểm thử trong môi trường và với bộ dữ liệu đã xác định. Thời gian xử lý của ngân hàng và thời gian dịch vụ SePay gửi webhook không thuộc thời gian phản hồi của các thao tác nội bộ BlueMoon.

4. ## Logical Database Requirement

Cơ sở dữ liệu MySQL phải lưu trữ và quản lý các nhóm dữ liệu chính sau:

* **Tài khoản và phân quyền:** Thông tin tài khoản, thông tin xác thực, vai trò và trạng thái sử dụng tài khoản.  
* **Căn hộ:** Mã căn hộ, diện tích, trạng thái hoạt động và các thông tin liên quan.  
* **Cư dân và lịch sử cư trú:** Hồ sơ cư dân, quan hệ cư trú với căn hộ, thời gian cư trú và lịch sử biến động như chuyển đến, chuyển đi, tạm trú, tạm vắng.  
* **Khoản thu bắt buộc:** Loại khoản thu, kỳ thu, hạn nộp, phạm vi áp dụng, đơn giá, diện tích và số tiền được lưu tại thời điểm tạo nghĩa vụ.  
* **Đợt đóng góp tự nguyện:** Thông tin đợt vận động và các khoản đóng góp thực tế của căn hộ tham gia.  
* **Bản ghi nộp tiền:** Khoản thu liên quan, căn hộ, số tiền, phương thức, thời điểm, trạng thái, mã tham chiếu và mã giao dịch bên ngoài nếu có.  
* **Thông tin đối soát QR:** Dữ liệu cần thiết để liên kết yêu cầu chuyển khoản với giao dịch nhận từ SePay, kiểm soát giao dịch trùng và lưu các giao dịch cần kiểm tra thủ công.  
* **Thông báo:** Tiêu đề, nội dung, người tạo và đối tượng nhận thông báo.  
* **Khiếu nại:** Người gửi, nội dung, trạng thái xử lý và phản hồi.

### **Các yêu cầu toàn vẹn dữ liệu**

* Mỗi tài khoản phải có định danh phù hợp và được liên kết với hồ sơ nghiệp vụ tương ứng khi cần thiết.  
* Mã căn hộ và định danh tài khoản phải được kiểm tra tính duy nhất theo quy tắc nghiệp vụ.  
* Một căn hộ không được có nhiều nghĩa vụ còn hiệu lực trùng loại khoản thu và kỳ thu.  
* Khoản thu phải lưu lại diện tích, đơn giá và số tiền tại thời điểm tạo để bảo toàn dữ liệu lịch sử.  
* Bản ghi khoản thu hoặc nộp tiền đã hủy phải được giữ lại; dữ liệu hủy không được tính vào thống kê tương ứng.  
* Tổng tiền đã thu phải được tính từ các bản ghi nộp tiền còn hiệu lực.  
* Đóng góp tự nguyện chỉ được ghi nhận khi có khoản tiền thực tế; không tạo công nợ cho căn hộ không tham gia.  
* Giao dịch QR phải được kiểm tra tính hợp lệ và chống xử lý trùng trước khi tạo bản ghi nộp tiền.  
* Các quan hệ dữ liệu phải bảo đảm tính nhất quán giữa tài khoản, cư dân, căn hộ, khoản thu và giao dịch thanh toán.  
* Việc cập nhật trạng thái thanh toán và tạo bản ghi nộp tiền phải được xử lý nhất quán, tránh trường hợp hệ thống báo đã thanh toán nhưng bản ghi giao dịch không được lưu thành công.

  5. ## Design Constraints

* **Ngôn ngữ và công nghệ:** Backend sử dụng Java với Spring Boot; frontend sử dụng ReactJS.  
* **Cơ sở dữ liệu:** Sử dụng MySQL để lưu trữ dữ liệu nghiệp vụ.  
* **Kiến trúc:** Backend tổ chức theo mô hình MVC và phân chia trách nhiệm theo các lớp Controller – Service – Repository.  
* **Phân quyền:** Hệ thống phải hỗ trợ ba vai trò BQT, NV và Cư dân; quyền thao tác và truy cập dữ liệu phải được kiểm tra ở backend.  
* **Quản lý tài khoản:** Tài khoản được cấp bởi người có thẩm quyền; không cung cấp chức năng tự đăng ký tài khoản cho cư dân.  
* **Bảo mật:** Mật khẩu phải được lưu dưới dạng băm an toàn. Các chức năng nhạy cảm phải kiểm tra quyền; thông tin xác thực và dữ liệu nhạy cảm không được ghi vào log theo cách làm lộ thông tin.  
* **Giao tiếp hệ thống:** Frontend và backend trao đổi dữ liệu qua API. Môi trường triển khai sử dụng HTTPS; webhook SePay phải được xác thực trước khi xử lý.  
* **Thanh toán:** Hệ thống hỗ trợ chuyển khoản QR và tích hợp SePay webhook để tiếp nhận, đối soát giao dịch. Không coi việc tạo hoặc quét mã QR là bằng chứng thanh toán thành công.  
* **Tính toàn vẹn:** Phải ngăn tạo trùng nghĩa vụ, xử lý trùng giao dịch và cập nhật sai số liệu thống kê.  
* **Phạm vi triển khai:** Hệ thống phục vụ quản lý chung cư BlueMoon; các chức năng như tích hợp VNPay/MoMo, gửi SMS/email/push notification và xuất báo cáo Excel/PDF không thuộc phạm vi hiện tại.

  6. ## Organizing the Specific Requirements

Để tối ưu khả năng hiểu và kiểm tra, các yêu cầu phần mềm được tổ chức theo chức năng chính của hệ thống:

* **Quản lý tài khoản và phân quyền:** Xử lý đăng nhập, đăng xuất, đổi mật khẩu, quản lý hồ sơ cá nhân, cấp tài khoản và kiểm soát quyền truy cập.  
* **Quản lý căn hộ:** Xử lý tạo mới, cập nhật, tra cứu và quản lý trạng thái sử dụng của căn hộ.  
* **Quản lý cư dân và biến động cư trú:** Quản lý hồ sơ cư dân, quan hệ cư trú với căn hộ và lịch sử chuyển đến, chuyển đi, tạm trú, tạm vắng.  
* **Quản lý khoản thu bắt buộc:** Quản lý loại khoản thu, kỳ thu, hạn nộp, đơn giá, phạm vi áp dụng và số tiền phải thu.  
* **Quản lý đóng góp tự nguyện:** Quản lý các đợt vận động và ghi nhận số tiền thực tế từ các căn hộ tham gia.  
* **Quản lý thu tiền và thanh toán QR:** Ghi nhận tiền mặt/chuyển khoản, tạo thông tin QR, tiếp nhận webhook SePay, đối soát giao dịch và xử lý ngoại lệ.  
* **Tra cứu và thống kê:** Tra cứu khoản thu, lịch sử nộp tiền, tổng phải thu, tổng đã thu, số tiền còn phải thu và tình hình đóng góp.  
* **Quản lý thông báo:** Tạo, phân phối, xem và xóa thông báo theo quyền.  
* **Quản lý khiếu nại và phản hồi:** Tiếp nhận khiếu nại của cư dân, cập nhật trạng thái xử lý và ghi nhận phản hồi.

Các yêu cầu chức năng cần được liên kết với Use Case và trường hợp kiểm thử tương ứng để bảo đảm có thể kiểm tra và nghiệm thu. Quyền của từng vai trò phải được xác định nhất quán giữa yêu cầu chức năng, thiết kế hệ thống và triển khai thực tế.

7. ## Use Case Model

   1. ### Tổng quan hệ thống

      2. ### Đăng nhập hệ thống

![][image2]

Mô tả Use Case:

| Mã Use case | UC002 |  |  |  | Tên Use case | Đăng nhập |  |
| :---- | :---- | ----- | :---- | :---- | :---- | :---- | :---- |
| **Tác nhân** | User (bao gồm BQT, Nhân viên, Cư dân) |  |  |  |  |  |  |
| **Mô tả** | Tác nhân đăng nhập vào hệ thống để truy cập các chức năng theo vai trò và quyền được cấp. |  |  |  |  |  |  |
| **Tiền điều kiện** | Tác nhân đã được cấp tài khoản và có mật khẩu tương ứng |  |  |  |  |  |  |
| **Luồng sự kiện chính (Thành công)** |  | **STT** | **Thực hiện bởi** | **Hành động** |  |  |  |
|  |  | 1\. | User | Truy cập vào trang đăng nhập của hệ thống. |  |  |  |
|  |  | 2\. | Hệ thống | Hiển thị giao diện đăng nhập (trường username và password). |  |  |  |
|  |  | 3\. | User | Nhập username và mật khẩu vào các trường tương ứng. |  |  |  |
|  |  | 4\. | User | Nhấn nút "Đăng nhập". |  |  |  |
|  |  | 5\. | Hệ thống | Kiểm tra tính hợp lệ của thông tin đăng nhập (username và password). |  |  |  |
|  |  | 6\. | Hệ thống | Xác thực thành công thông tin đăng nhập. |  |  |  |
|  |  | 7\. | Hệ thống | Chuyển hướng User đến trang chính/dashboard sau khi đăng nhập thành công. |  |  |  |
| **Luồng sự kiện thay thế** |  | **STT** | **Thực hiện bởi** | **Hành động** |  |  |  |
|  |  | 3a | User | Chọn tùy chọn "Nhớ tài khoản". |  |  |  |
|  |  | 3a.1 | Hệ thống | Hệ thống có thể ghi nhớ tên đăng nhập nhưng không lưu mật khẩu dạng rõ  |  |  |  |
|  |  | 5a | Hệ thống | Xác thực thông tin đăng nhập thất bại (username hoặc password không đúng). |  |  |  |
|  |  | 5a.1 | Hệ thống | **Thông báo lỗi:** "Username hoặc mật khẩu không đúng. Vui lòng thử lại.". |  |  |  |
|  |  | 5a.2 | Hệ thống | Yêu cầu User nhập lại thông tin đăng nhập.  |  |  |  |
| **Hậu điều kiện** |  | **Thành công:** User được đăng nhập vào hệ thống và truy cập được các chức năng theo quyền được cấp. **Thất bại:** User vẫn ở trang đăng nhập và nhận được thông báo lỗi. **Quên mật khẩu:** Mật khẩu của User được cập nhật hoặc User được thông báo lỗi.  |  |  |  |  |  |

Biểu đồ hoạt động:

![][image3]	

3. ### Quản lý thông tin cư dân và căn hộ

![][image4]

Mô tả Use Case:

| Mã Use case | UC003 |  |  |  | Tên Use case | Quản lý thông tin cư dân và căn hộ |  |
| :---- | :---- | ----- | :---- | :---- | :---- | :---- | :---- |
| **Tác nhân** | User (bao gồm BQT, Nhân viên, Cư dân) |  |  |  |  |  |  |
| **Mô tả** | Cho phép BQT/Nhân viên quản lý thông tin cư dân, thành viên hộ gia đình, thông tin căn hộ và lịch sử cư trú. Cư dân chỉ được xem thông tin thuộc hộ gia đình mình |  |  |  |  |  |  |
| **Tiền điều kiện** | Người dùng đã đăng nhập và được cấp quyền truy cập chức năng tương ứng |  |  |  |  |  |  |
| **Luồng sự kiện chính (Thành công)** |  | **STT** | **Thực hiện bởi** | **Hành động** |  |  |  |
|  |  | 1\. | BQT/Nhân viên  | Truy cập chức năng quản lý thông tin cư dân và căn hộ. |  |  |  |
|  |  | 2\. | Hệ thống | Hiển thị danh sách và các chức năng quản lý. |  |  |  |
|  |  | 3\. | BQT/Nhân viên  | Chọn nghiệp vụ quản lý: cư dân hoặc căn hộ. |  |  |  |
|  |  | 4\. | Hệ thống  | Hiển thị danh sách tương ứng với nghiệp vụ đã chọn. |  |  |  |
|  |  | 5\. | BQT/Nhân viên  | Chọn thêm mới hoặc chọn đối tượng cần xem, cập nhật thông tin. |  |  |  |
|  |  | 6\. | Hệ thống | Hiển thị biểu mẫu hoặc thông tin của đối tượng được chọn. |  |  |  |
|  |  | 7\. | BQT/Nhân viên | Nhập/cập nhật thông tin theo nghiệp vụ đã chọn  |  |  |  |
|  |  | 8\. | Hệ thống  | Kiểm tra tính hợp lệ của dữ liệu |  |  |  |
|  |  | 9\. | Hệ thống | Lưu thông tin theo nghiệp vụ đã chọn  |  |  |  |
|  |  | 10\. | Hệ thống | Thông báo thao tác thành công và cập nhật danh sách tương ứng. |  |  |  |
| **Luồng sự kiện thay thế** |  | **STT** | **Thực hiện bởi** | **Hành động** |  |  |  |
|  |  | 3a | BQT/Nhân viên  | Chọn xem chi tiết một cư dân hoặc căn hộ.  |  |  |  |
|  |  | 3a.1 | Hệ thống | Hiển thị thông tin chi tiết và lịch sử cư trú liên quan. |  |  |  |
|  |  | 3b | Cư dân | Truy cập chức năng xem thông tin hộ gia đình.  |  |  |  |
|  |  | 3b.1 | Hệ thống | Kiểm tra quyền truy cập và chỉ hiển thị thông tin thuộc hộ gia đình của cư dân đó.  |  |  |  |
|  |  | 3b.2 | Hệ thống  | Từ chối truy cập nếu cư dân yêu cầu xem dữ liệu của hộ gia đình khác.  |  |  |  |
|  |  | 8a | Hệ thống | Phát hiện dữ liệu không hợp lệ hoặc thiếu thông tin bắt buộc.  |  |  |  |
|  |  | 8a.1 | Hệ thống | Thông báo lỗi và yêu cầu BQT/Nhân viên nhập lại thông tin.  |  |  |  |
|  |  | 8a.2 | BQT/Nhân viên  | Chỉnh sửa dữ liệu và gửi lại để hệ thống kiểm tra.  |  |  |  |
| **Hậu điều kiện** |  | **Thành công:** Thông tin cư dân hoặc căn hộ được lưu/cập nhật; lịch sử cư trú được bảo toàn khi có thay đổi. **Thất bại:** Hệ thống không lưu dữ liệu không hợp lệ và hiển thị thông báo lỗi.  **Cư dân xem thông tin:** Cư dân chỉ xem được thông tin thuộc hộ gia đình mình. **Ngừng sử dụng căn hộ:** Cập nhật trạng thái căn hộ thành *Ngừng sử dụng,* không xóa vật lý dữ liệu.  |  |  |  |  |  |

Biểu đồ hoạt động:

![][image5]

4. ### Quản lý khoản thu và đóng góp

![][image6]

Mô tả Use Case:

| Mã Use case | UC004 |  |  |  | Tên Use case | Quản lý khoản thu và đóng góp |  |
| :---- | :---- | ----- | :---- | :---- | :---- | :---- | :---- |
| **Tác nhân** | User (bao gồm BQT, Nhân viên) |  |  |  |  |  |  |
| **Mô tả** | Cho phép BQT/Nhân viên tạo và quản lý các đợt thu bắt buộc, đợt đóng góp tự nguyện; xem thông tin và cập nhật các nội dung được phép chỉnh sửa. |  |  |  |  |  |  |
| **Tiền điều kiện** | Người dùng đã đăng nhập và được cấp quyền truy cập chức năng tương ứng |  |  |  |  |  |  |
| **Luồng sự kiện chính (Thành công)** |  | **STT** | **Thực hiện bởi** | **Hành động** |  |  |  |
|  |  | 1\. | BQT/Nhân viên  | Truy cập chức năng quản lý khoản thu và đóng góp.  |  |  |  |
|  |  | 2\. | Hệ thống | Hiển thị danh sách các đợt thu và đợt đóng góp.  |  |  |  |
|  |  | 3\. | BQT/Nhân viên  | Chọn tạo đợt thu bắt buộc hoặc tạo đợt đóng góp tự nguyện.  |  |  |  |
|  |  | 4\. | Hệ thống  | Hiển thị biểu mẫu theo loại nghiệp vụ đã chọn.  |  |  |  |
|  |  | 5\. | BQT/Nhân viên  | Nếu là khoản thu bắt buộc: nhập kỳ thu, hạn nộp và đối tượng áp dụng. Nếu là đóng góp tự nguyện: nhập nội dung, mục đích và thông tin đợt đóng góp.  |  |  |  |
|  |  | 6\. | Hệ thống | Kiểm tra dữ liệu đầu vào và điều kiện tạo đợt thu.  |  |  |  |
|  |  | 7\. | Hệ thống | Với khoản thu bắt buộc, xác định các hộ/căn hộ thuộc đối tượng phải nộp và tính số tiền theo diện tích, đơn giá áp dụng.  |  |  |  |
|  |  | 8\. | Hệ thống  | Lưu đợt thu hoặc đợt đóng góp; với khoản thu bắt buộc, lưu lại diện tích, đơn giá và số tiền tại thời điểm tạo.  |  |  |  |
|  |  | 9\. | Hệ thống | Thông báo tạo thành công và cập nhật danh sách.  |  |  |  |
| **Luồng sự kiện thay thế** |  | **STT** | **Thực hiện bởi** | **Hành động** |  |  |  |
|  |  | 3a | BQT/Nhân viên  | Chọn cập nhật thông tin đợt thu hoặc đợt đóng góp.   |  |  |  |
|  |  | 3a.1 | Hệ thống | Hiển thị thông tin hiện tại của đợt được chọn.  |  |  |  |
|  |  | 3a.2 | BQT/Nhân viên | Cập nhật hạn nộp, mô tả hoặc mục đích đóng góp trong phạm vi được phép.  |  |  |  |
|  |  | 3a.3 | Hệ thống | Kiểm tra và lưu thay đổi |  |  |  |
|  |  | 3b | BQT | Chọn hủy một đợt thu hoặc đợt đóng góp.   |  |  |  |
|  |  | 3c | Nhân viên | Thử hủy đợt thu hoặc đợt đóng góp.  |  |  |  |
|  |  | 3c.1 | Hệ thống  | Từ chối thao tác do Nhân viên không có quyền hủy.   |  |  |  |
|  |  | 6a | Hệ thống | Phát hiện nghĩa vụ trùng loại khoản thu, hộ gia đình và kỳ thu theo quy tắc nghiệp vụ.   |  |  |  |
|  |  | 6a.1 | Hệ thống | Thông báo lỗi và yêu cầu nhập lại thông tin.   |  |  |  |
|  |  | 6a.2 | BQT/Nhân viên  | Chỉnh sửa thông tin và gửi lại để hệ thống kiểm tra.   |  |  |  |
| **Hậu điều kiện** |  | **Thành công:** Đợt thu hoặc đợt đóng góp được lưu và hiển thị trong danh sách.  **Thất bại:** Dữ liệu không hợp lệ không được lưu; hệ thống thông báo lỗi.   **Hủy đợt:** Đợt thu/đóng góp được ghi nhận trạng thái hủy, không xóa vật lý lịch sử. **Khoản thu bắt buộc:** Số tiền đã phát sinh không tự thay đổi khi đơn giá hoặc thông tin căn hộ thay đổi về sau. **Đóng góp tự nguyện:** Việc không tham gia đóng góp không tạo công nợ bắt buộc. |  |  |  |  |   |

### Biểu đồ hoạt động

![][image7]

5. ### Thanh toán và theo dõi thu phí

![][image8]

Mô tả Use Case:

| Mã Use case | UC005 |  |  |  | Tên Use case | Thanh toán và theo dõi thu phí  |  |
| :---- | :---- | ----- | :---- | :---- | :---- | :---- | :---- |
| **Tác nhân** | User (bao gồm BQT, Nhân viên, Cư dân) và Sepay |  |  |  |  |  |  |
| **Mô tả** | Cho phép cư dân thanh toán khoản thu bắt buộc bằng QR, đóng góp tự nguyện, xem công nợ và lịch sử thanh toán. BQT/Nhân viên ghi nhận thanh toán thủ công, xử lý giao dịch cần kiểm tra và theo dõi tình hình thu phí.  |  |  |  |  |  |  |
| **Tiền điều kiện** | Người dùng đã đăng nhập và có quyền truy cập chức năng tương ứng. Khoản thu hoặc đợt đóng góp đã được tạo.  |  |  |  |  |  |  |
| **Luồng sự kiện chính (Thành công)** |  | **STT** | **Thực hiện bởi** | **Hành động** |  |  |  |
|  |  | 1\. | Người dùng | Người dùng truy cập chức năng thanh toán và theo dõi thu phí   |  |  |  |
|  |  | 2\. | Hệ thống | Hiển thị chức năng và dữ liệu theo quyền của người dùng.  |  |  |  |
|  |  | 3\. | Cư dân | Chọn khoản phải thu để thanh toán, hoặc chọn đợt đóng góp tự nguyện.  |  |  |  |
|  |  | 4\. | Hệ thống  | Với khoản bắt buộc, hiển thị số tiền còn phải nộp và mã tham chiếu; với đóng góp tự nguyện, cho phép cư dân nhập số tiền muốn đóng góp.   |  |  |  |
|  |  | 5\. | Hệ thống | Tạo mã QR thanh toán với thông tin tài khoản nhận và mã tham chiếu tương ứng. |  |  |  |
|  |  | 6\. | Cư dân / SePay  | Cư dân thực hiện giao dịch; SePay gửi thông báo giao dịch đến hệ thống.   |  |  |  |
|  |  | 7\. | Hệ thống | Đối soát giao dịch dựa trên tài khoản nhận, mã tham chiếu, số tiền và thông tin giao dịch trùng lặp.  |  |  |  |
|  |  | 8\. | Hệ thống  | Nếu giao dịch hợp lệ, ghi nhận thanh toán hoặc khoản đóng góp và cập nhật dữ liệu thu phí. |  |  |  |
|  |  | 9\. | Hệ thống | Cập nhật công nợ, lịch sử thanh toán và số liệu thống kê tương ứng.  |  |  |  |
| **Luồng sự kiện thay thế** |  | **STT** | **Thực hiện bởi** | **Hành động** |  |  |  |
|  |  | 3a | BQT/Nhân viên  | Chọn ghi nhận thanh toán thủ công sau khi xác minh tiền mặt hoặc giao dịch chuyển khoản thông thường.    |  |  |  |
|  |  | 3a.1 | BQT/Nhân viên  | Chọn khoản thu hoặc đợt đóng góp, nhập số tiền và thông tin giao dịch.   |  |  |  |
|  |  | 3a.2 | Hệ thống  | Kiểm tra dữ liệu, lưu bản ghi hợp lệ và cập nhật số liệu thu phí.   |  |  |  |
|  |  | 7a | Hệ thống | Phát hiện giao dịch thiếu thông tin, sai số tiền, sai mã tham chiếu hoặc không xác định được khoản cần thanh toán.  |  |  |  |
|  |  | 7a.1 | Hệ thống  | Đánh dấu giao dịch cần kiểm tra; chưa tự động ghi nhận thanh toán thành công.    |  |  |  |
|  |  | 7a.2 | BQT/Nhân viên | Kiểm tra giao dịch và xử lý theo quyền được cấp.   |  |  |  |
|  |  | 7b  | Hệ thống  | Nhận lại thông báo webhook trùng với giao dịch đã xử lý.  |  |  |  |
|  |  | 7b.1 | Hệ thống | Không tạo thêm bản ghi thanh toán trùng lặp.   |  |  |  |
|  |  | 8a  | BQT | Chọn hủy bản ghi thanh toán.    |  |  |  |
|  |  | 8a.1 | Hệ thống | Ghi nhận trạng thái hủy, bảo toàn lịch sử và loại bản ghi đó khỏi tổng tiền đã thu.    |  |  |  |
|  |  | 8b | Nhân viên | Thử hủy bản ghi thanh toán. |  |  |  |
|  |  | 8b.1 | Hệ thống | Từ chối thao tác do không đủ quyền. |  |  |  |
|  |  | 9a | Cư dân | Xem công nợ hoặc lịch sử thanh toán. |  |  |  |
|  |  | 9a.1 | Hệ thống | Chỉ hiển thị dữ liệu thuộc hộ gia đình của cư dân đó. |  |  |  |
| **Hậu điều kiện** |  | **Thanh toán thành công:** Bản ghi thanh toán được lưu, công nợ và số liệu thu phí được cập nhật. **Giao dịch cần kiểm tra:** Chưa ghi nhận thanh toán thành công cho đến khi giao dịch được xác minh. **Giao dịch trùng webhook:** Không tạo bản ghi thanh toán trùng lặp. **Hủy bản ghi:** Lịch sử vẫn được lưu; bản ghi bị hủy không được tính vào tổng tiền đã thu. **Đóng góp tự nguyện:** Chỉ ghi nhận số tiền thực tế đóng góp; không tham gia không phát sinh công nợ. **Quyền truy cập:** Cư dân chỉ xem dữ liệu của hộ mình; báo cáo toàn hệ thống dành cho BQT/Nhân viên theo quyền. |  |  |  |  |   |

Biểu đồ hoạt động  
![][image9]

6. ### Thông báo và phản hồi cư dân

![][image10]

Mô tả Use Case

| Mã Use case | UC006 |  |  |  | Tên Use case | Thông báo và phản hồi cư dân  |  |
| :---- | :---- | ----- | :---- | :---- | :---- | :---- | :---- |
| **Tác nhân** | User (bao gồm BQT, Nhân viên, Cư dân) |  |  |  |  |  |  |
| **Mô tả** | Cho phép BQT/Nhân viên tạo thông báo, thiết lập nhắc phí tự động và xử lý khiếu nại. Cư dân xem thông báo được gửi đến mình, gửi khiếu nại và theo dõi phản hồi.  |  |  |  |  |  |  |
| **Tiền điều kiện** | Người dùng đã đăng nhập và có quyền truy cập chức năng tương ứng. |  |  |  |  |  |  |
| **Luồng sự kiện chính (Thành công)** |  | **STT** | **Thực hiện bởi** | **Hành động** |  |  |  |
|  |  | 1\. | Người dùng | Truy cập chức năng thông báo và phản hồi cư dân.   |  |  |  |
|  |  | 2\. | Hệ thống | Hiển thị các chức năng tương ứng với quyền người dùng.  |  |  |  |
|  |  | 3\. | Người dùng | Chọn nghiệp vụ cần thực hiện.  |  |  |  |
|  |  | A. Tạo thông báo |  |  |  |  |  |
|  |  | 4\. | BQT/Nhân viên | Nhập tiêu đề, nội dung và đối tượng nhận thông báo.  |  |  |  |
|  |  | 5\. | Hệ thống  | Kiểm tra dữ liệu thông báo.  |  |  |  |
|  |  | 6\. | Hệ thống | Lưu thông báo, gửi đến đúng đối tượng trong ứng dụng và lưu lịch sử gói.  |  |  |  |
|  |  | B. Xử lý khiếu nại |  |  |  |  |  |
|  |  | 7\. | Cư dân  | Nhập tiêu đề và nội dung khiếu nại. |  |  |  |
|  |  | 8\. | Hệ thống | Kiểm tra và lưu khiếu nại với trạng thái Mới.  |  |  |  |
|  |  | 9\. | BQT/Nhân viên | Tiếp nhận, xử lý khiếu nại và nhập phản hồi. |  |  |  |
|  |  | 10\. | Hệ thống | Lưu phản hồi, cập nhật trạng thái và thông báo kết quả cho cư dân trong ứng dụng. |  |  |  |
| **Luồng sự kiện thay thế** |  | **STT** | **Thực hiện bởi** | **Hành động** |  |  |  |
|  |  | 3a | BQT  | Chọn xóa thông báo.  |  |  |  |
|  |  | 3a.1 | Hệ thống  | Xóa thông báo theo quyền được cấp và cập nhật danh sách.  |  |  |  |
|  |  | 3b | Nhân viên | Thử xóa thông báo.  |  |  |  |
|  |  | 3b.1 | Hệ thống | Từ chối thao tác do Nhân viên không có quyền xóa.  |  |  |  |
|  |  | 4a  | Hệ thống  | Phát hiện thông báo thiếu tiêu đề, nội dung hoặc đối tượng nhận hợp lệ.  |  |  |  |
|  |  | 4a.1 | Hệ thống | Thông báo lỗi và yêu cầu BQT/Nhân viên chỉnh sửa.  |  |  |  |
|  |  | 7a  | Hệ thống  | Phát hiện khiếu nại thiếu tiêu đề hoặc nội dung.  |  |  |  |
|  |  | 7a.1 | Hệ thống | Thông báo lỗi và yêu cầu cư dân nhập lại. |  |  |  |
|  |  | 9a  | BQT/Nhân viên | Cập nhật trạng thái khiếu nại thành “Đang xử lý” hoặc “Đã giải quyết”. |  |  |  |
|  |  | 9a.1 | Hệ thống | Lưu trạng thái mới và hiển thị cho cư dân có khiếu nại tương ứng.  |  |  |  |
|  |  | 10a | Cư dân | Xem khiếu nại đã gửi. |  |  |  |
|  |  | 10a.1 | Hệ thống | Chỉ hiển thị khiếu nại và phản hồi thuộc về cư dân đó.  |  |  |  |
| **Hậu điều kiện** |  | **Thông báo:** Được lưu và gửi đến đúng đối tượng nhận; lịch sử gửi được lưu trong hệ thống. **Nhắc phí:** Hệ thống gửi nhắc phí tự động theo lịch đã cấu hình và ghi nhận lịch sử thông báo trong ứng dụng. **Khiếu nại:** Được lưu và theo dõi theo các trạng thái “Mới”, “Đang xử lý”, “Đã giải quyết”. **Phản hồi:** Cư dân có thể xem nội dung phản hồi và trạng thái khiếu nại của mình. **Quyền truy cập:** Cư dân không thể xem khiếu nại của người khác. Phạm vi thông báo: Chỉ gửi thông báo trong ứng dụng, không bao gồm email, SMS hoặc thông báo đẩy. |  |  |  |  |  |

Biểu đồ hoạt động

![][image11]

8. ## Additional Comments

Các yêu cầu cụ thể trên đây được xây dựng dựa trên các tiêu chuẩn và quy định nội bộ của dự án cũng như các hướng dẫn từ các tài liệu tham khảo liên quan.

Trong quá trình phát triển và kiểm thử, mỗi yêu cầu cần được kiểm tra độc lập với các kịch bản (test cases) tương ứng nhằm đảm bảo tính đầy đủ và chính xác của hệ thống.

Bất kỳ thay đổi nào trong yêu cầu sẽ được ghi nhận và cập nhật trong tài liệu SRS để đảm bảo sự nhất quán qua các phiên bản phát hành.

4. # Other Non-functional Requirements

   1. ## Performance Requirements

      1. ### Đăng nhập hệ thống

**Thời gian phản hồi:** Không quá 3 giây.

**Lý do:** Đăng nhập là thao tác cơ bản, cần phản hồi nhanh để người dùng truy cập hệ thống thuận tiện.

2. ### Tạo tài khoản

**Thời gian phản hồi:** Không quá 3 giây.

**Lý do:** Việc tạo tài khoản là thao tác xử lý dữ liệu thông thường. Hệ thống cần kiểm tra dữ liệu đầu vào, tính hợp lệ và tính duy nhất của định danh tài khoản trước khi lưu.

*Lưu ý: Đây là chức năng tạo tài khoản bởi BQT/NV theo quyền được cấp, không phải đăng ký tự do của người dùng.*

3. ### Quản lý thông tin cư dân

**Thời gian phản hồi:** Không quá 3 giây đối với thao tác xem, thêm, cập nhật và tra cứu thông tin thông thường.

**Lý do:** Thông tin cư dân được sử dụng thường xuyên trong các nghiệp vụ quản lý chung cư nên cần được truy xuất và cập nhật kịp thời.

4. ### Quản lý thông tin căn hộ

**Thời gian phản hồi:** Không quá 3 giây đối với thao tác xem, thêm, cập nhật và tìm kiếm căn hộ thông thường.

**Lý do:** Hệ thống cần phản hồi nhanh khi BQT/NV quản lý danh sách căn hộ, tra cứu mã căn hộ và cập nhật trạng thái sử dụng.

5. ### Tạo khoản thu

**Thời gian phản hồi:** Không quá 3 giây đối với thao tác tạo khoản thu thông thường.

**Lý do:** Hệ thống cần kiểm tra thông tin khoản thu, tính số tiền theo diện tích và đơn giá áp dụng, đồng thời ngăn tạo trùng nghĩa vụ. Với thao tác áp dụng cho nhiều căn hộ, thời gian cần được kiểm thử trên bộ dữ liệu đã thống nhất.

6. ### Cập nhật khoản thu

**Thời gian phản hồi:** Không quá 3 giây đối với thao tác cập nhật thông tin khoản thu thông thường.

**Lý do:** Hệ thống cần lưu thông tin hợp lệ và bảo đảm tính nhất quán của dữ liệu. Việc cập nhật không được làm thay đổi diện tích, đơn giá và số tiền đã lưu tại thời điểm tạo khoản thu trái với quy tắc nghiệp vụ.

7. ### Ghi nhận thanh toán

**Thời gian phản hồi:** Không quá 3 giây đối với thao tác ghi nhận thanh toán thông thường trên hệ thống.

**Lý do:** Hệ thống cần lưu chính xác thông tin nộp tiền và cập nhật trạng thái nghĩa vụ tương ứng. Thời gian này không bao gồm thời gian xử lý giao dịch của ngân hàng hoặc thời gian SePay gửi webhook.

8. ### Tạo và hiển thị mã QR thanh toán

**Thời gian phản hồi:** Không quá 3 giây.

**Lý do:** Cư dân cần nhận được thông tin chuyển khoản QR nhanh chóng để thực hiện thanh toán. Thời gian xử lý thực tế của ngân hàng và thời gian nhận webhook không thuộc ngưỡng này.

9. Tra cứu và lọc dữ liệu

**Thời gian phản hồi:** Không quá 3 giây đối với thao tác tìm kiếm/lọc thông thường.

**Lý do:** BQT/NV cần tra cứu nhanh thông tin căn hộ, cư dân, khoản thu và lịch sử nộp tiền phục vụ công tác quản lý.

10. Thống kê thu phí và đóng góp

**Thời gian phản hồi:** Không quá 5 giây.

**Lý do:** Thống kê cần tổng hợp nhiều bản ghi để tính tổng phải thu, tổng đã thu, số tiền còn phải thu và tình hình đóng góp tự nguyện. Do đó, ngưỡng thời gian cao hơn các thao tác dữ liệu thông thường.

2. ## Safety and Security Requirements

   1. ### Yêu Cầu An Toàn

* Hệ thống phải đảm bảo các thao tác nghiệp vụ được thực hiện nhất quán; không cập nhật trạng thái đã nộp tiền nếu việc lưu bản ghi thanh toán thất bại.  
* Hệ thống phải ngăn tạo trùng nghĩa vụ thu phí cho cùng căn hộ, loại khoản thu và kỳ thu.  
* Khi hủy khoản thu hoặc bản ghi nộp tiền, hệ thống phải lưu giữ lịch sử cần thiết và cập nhật thống nhất thông tin tra cứu, thống kê.  
* Hệ thống phải kiểm tra dữ liệu đầu vào ở phía backend để hạn chế dữ liệu không hợp lệ và thao tác sai quy tắc nghiệp vụ.  
* Với thanh toán QR, hệ thống không được tự động xác nhận đã nộp tiền khi chưa có cơ chế xác nhận hợp lệ theo quy trình đã thống nhất.

  2. ### Yêu Cầu Bảo Mật

* Người dùng phải đăng nhập trước khi truy cập các chức năng nội bộ của hệ thống.  
* Backend phải kiểm tra quyền truy cập đối với từng thao tác; không chỉ dựa vào việc ẩn chức năng trên giao diện.  
* Cư dân chỉ được xem dữ liệu liên quan đến bản thân, căn hộ hiện tại và các khiếu nại do chính mình gửi.  
* Mật khẩu phải được lưu dưới dạng băm an toàn; không lưu mật khẩu dạng văn bản thuần hoặc trả mật khẩu trong phản hồi API.  
* Thông tin xác thực và dữ liệu nhạy cảm không được ghi vào log theo cách làm lộ thông tin.  
* Dữ liệu trao đổi giữa trình duyệt và hệ thống phải được truyền qua HTTPS trong môi trường triển khai.   
* Webhook thanh toán phải được kiểm tra tính hợp lệ trước khi xử lý; thông tin bí mật như token hoặc secret không được ghi vào log hoặc phản hồi.

  3. ## Software Quality Attributes

     1. ### Tính Chính Xác

* Dữ liệu cư dân, thông tin thanh toán và lịch sử giao dịch phải được lưu trữ và hiển thị chính xác.  
* Hệ thống phải đảm bảo tính toàn vẹn dữ liệu, tránh mất mát hoặc sai lệch thông tin.  
* Các số liệu thống kê về khoản phải thu, đã thu và còn phải thu phải được tính nhất quán theo quy tắc nghiệp vụ.

  2. ### Tính Khả Dụng

* Hệ thống phải cung cấp các chức năng phù hợp với quyền của BQT, nhân viên quản lý và cư dân.  
* Các chức năng chính như đăng nhập, tra cứu thông tin, quản lý khoản thu và ghi nhận thanh toán phải hoạt động đúng theo yêu cầu đã đặc tả.  
* Hệ thống không quy định cam kết uptime cụ thể; khả năng hoạt động được đánh giá thông qua kiểm thử các chức năng trong phạm vi dự án.

  3. ### Tính Linh Hoạt

* Giao diện phải có khả năng sử dụng trên các thiết bị có trình duyệt web hiện đại, gồm máy tính và thiết bị di động.  
* Hệ thống phải hỗ trợ thay đổi các thông tin nghiệp vụ được cho phép, chẳng hạn đơn giá thu phí, mà không làm thay đổi sai lệch các khoản thu đã được tạo trước đó.  
* Các chức năng và quyền truy cập phải được tổ chức theo vai trò người dùng, giúp việc điều chỉnh quy tắc nghiệp vụ được thực hiện có kiểm soát.

  4. ### Tính Bảo Trì

* Mã nguồn phải được tổ chức theo kiến trúc đã chọn: ReactJS cho giao diện, Java Spring Boot cho backend và MySQL cho cơ sở dữ liệu.  
* Các quy tắc nghiệp vụ quan trọng như tính phí, kiểm tra quyền, xử lý webhook thanh toán và thống kê phải được tách rõ khỏi phần trình bày giao diện.  
* Khi thay đổi yêu cầu, nhóm phải cập nhật các tài liệu liên quan như SRS, Use Case, thiết kế dữ liệu/API và test case.

  5. ### Tính Tương Tác

* Giao diện phải hiển thị rõ kết quả của thao tác, trạng thái xử lý và thông báo khi thao tác thành công hoặc thất bại.  
* Thông báo lỗi và xác nhận thao tác phải sử dụng tiếng Việt rõ ràng, đồng thời hướng dẫn bước tiếp theo nếu cần.  
* Các thuật ngữ và trạng thái trên giao diện phải được sử dụng nhất quán, tránh nhầm lẫn giữa khoản thu, kỳ thu, đợt đóng góp và trạng thái nộp tiền.

  6. ### Tính Kiểm Thử Được

* Mỗi yêu cầu chức năng phải có thể liên kết với ít nhất một Use Case và test case tương ứng.  
* Các yêu cầu về phân quyền phải có thể kiểm tra bằng cách thử truy cập chức năng hoặc API bằng vai trò không được phép.  
* Các quy tắc tính phí, ghi nhận thanh toán, hủy bản ghi và thống kê phải có dữ liệu đầu vào và kết quả mong đợi để đối chiếu.  
* Các yêu cầu hiệu năng phải được kiểm tra trong môi trường và điều kiện dữ liệu được xác định trong kế hoạch kiểm thử.

  7. ### Tính Dễ Sử Dụng

* Giao diện phải phân chia chức năng phù hợp với từng vai trò BQT, nhân viên quản lý và cư dân.  
* Các chức năng thường dùng phải có tên gọi nhất quán với thuật ngữ nghiệp vụ của hệ thống.  
* Thông tin khoản thu, tình trạng nộp tiền, thông báo và khiếu nại phải được trình bày rõ ràng để người dùng dễ tra cứu.  
* Hệ thống phải hiển thị thông báo rõ ràng khi người dùng nhập dữ liệu không hợp lệ hoặc thực hiện thao tác không thành công.

5. # Other Requirements

Hệ thống cũng cần tuân theo các yêu cầu bổ sung sau:

* **Yêu cầu về cơ sở dữ liệu:**  
  * Sử dụng MySQL để lưu trữ dữ liệu của hệ thống.  
  * Cơ sở dữ liệu phải đảm bảo các ràng buộc toàn vẹn, khóa chính, khóa ngoại và tính nhất quán giữa các dữ liệu liên quan.  
  * Người dùng chỉ được truy cập dữ liệu thông qua ứng dụng và backend; không truy cập trực tiếp cơ sở dữ liệu thông qua giao diện người dùng.  
  * Dữ liệu khoản thu, thanh toán và lịch sử nghiệp vụ phải được lưu trữ phù hợp để phục vụ tra cứu và thống kê.  
* **Yêu cầu về quốc tế hóa:**  
  * Giao diện và thông báo của hệ thống sử dụng tiếng Việt.  
  * Các khoản tiền được biểu diễn bằng đồng Việt Nam (VND).  
  * Phiên bản hiện tại không yêu cầu hỗ trợ nhiều ngôn ngữ.  
* **Yêu cầu pháp lý:**  
  * Việc thu thập, lưu trữ và hiển thị thông tin cư dân phải phục vụ mục đích quản lý chung cư trong phạm vi dự án.  
  * Thông tin cá nhân và dữ liệu thanh toán phải được bảo vệ theo các yêu cầu bảo mật đã đặc tả.  
  * Các quy định pháp luật cụ thể áp dụng cho dữ liệu cá nhân và quản lý cư trú cần được xác định, đối chiếu khi triển khai thực tế; tài liệu hiện tại chưa đặc tả danh sách văn bản pháp luật cụ thể.  
* **Tái sử dụng phần mềm:**  
  * Nhóm được phép sử dụng các thư viện và framework phù hợp với công nghệ đã lựa chọn, gồm ReactJS, Java Spring Boot và các thư viện hỗ trợ cần thiết.  
  * Các thư viện bên ngoài phải có giấy phép sử dụng phù hợp và được tích hợp theo cách không ảnh hưởng đến yêu cầu chức năng, bảo mật và bảo trì của hệ thống.  
  * Các thành phần nghiệp vụ dùng chung nên được tổ chức để có thể tái sử dụng trong các chức năng liên quan, hạn chế lặp lại mã nguồn.