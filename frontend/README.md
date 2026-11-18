# BlueMoon Frontend

Frontend cho hệ thống quản lý chung cư BlueMoon.

## Công nghệ

- ReactJS 19
- JavaScript (ES6+) và JSX
- Vite
- Tailwind CSS v4
- React Router DOM
- Oxlint

## Lệnh sử dụng

Chạy trong thư mục `frontend/`:

```bash
npm install
npm run dev
```

| Lệnh | Mục đích |
| --- | --- |
| `npm run dev` | Chạy môi trường phát triển |
| `npm run build` | Tạo production build |
| `npm run lint` | Kiểm tra JavaScript và JSX |
| `npm run preview` | Xem production build cục bộ |

Mã nguồn trong `src/` sử dụng JavaScript/JSX. UI hiện dùng mock data và các
tương tác local state để trình diễn, chưa tích hợp nghiệp vụ hoặc API thực tế.
