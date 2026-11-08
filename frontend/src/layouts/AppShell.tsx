import { useState } from 'react'
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { Icon } from '../components/ui'

const navigation = [
  ['dashboard', 'Trang chủ', 'home'], ['apartments', 'Căn hộ', 'building'], ['residents', 'Hộ gia đình & cư dân', 'users'], ['residency', 'Biến động cư trú', 'move'], ['fees', 'Khoản thu', 'receipt'], ['payments', 'Thanh toán', 'wallet'], ['reports', 'Thống kê', 'chart'], ['notices', 'Thông báo', 'bell'], ['issues', 'Phản ánh', 'message'],
]
const labels: Record<string, string> = { dashboard: 'Tổng quan', apartments: 'Căn hộ', residents: 'Hộ gia đình & cư dân', residency: 'Biến động cư trú', fees: 'Khoản thu', payments: 'Thanh toán', reports: 'Thống kê', notices: 'Thông báo', issues: 'Phản ánh' }

export function AppShell() {
  const [open, setOpen] = useState(false); const [profile, setProfile] = useState(false); const navigate = useNavigate(); const segment = useLocation().pathname.split('/')[1] || 'dashboard'
  const item = (path: string, text: string, icon: string) => <NavLink key={path} to={`/${path}`} onClick={() => setOpen(false)} className={({ isActive }) => `flex h-10 items-center gap-3 rounded-ui px-3 text-sm font-semibold transition ${isActive ? 'bg-soft text-soft-ink' : 'text-mute hover:bg-soft/60'}`}><Icon name={icon} className="w-5 text-lg" />{text}</NavLink>
  return <div className="min-h-screen bg-canvas">
    {open && <button className="fixed inset-0 z-30 bg-slate-950/30 lg:hidden" aria-label="Đóng menu" onClick={() => setOpen(false)} />}
    <aside className={`fixed inset-y-0 left-0 z-40 flex w-64 flex-col border-r border-line bg-surface transition-transform lg:translate-x-0 ${open ? 'translate-x-0' : '-translate-x-full'}`}>
      <div className="flex h-[72px] items-center gap-3 px-5"><span className="flex size-10 items-center justify-center rounded-ui bg-primary text-xl text-white">▥</span><span><b className="block text-lg tracking-tight">BlueMoon</b><small className="text-[11px] text-mute">Quản lý chung cư</small></span></div>
      <nav className="flex-1 space-y-1 overflow-y-auto px-3 py-2" aria-label="Điều hướng chính">{navigation.map(([path, text, icon]) => item(path, text, icon))}</nav>
      <div className="border-t border-line p-3"><div className="flex items-center gap-3 p-2"><span className="flex size-9 items-center justify-center rounded-full bg-soft text-xs font-bold text-soft-ink">VA</span><span className="min-w-0"><b className="block truncate text-sm">Nguyễn Văn An</b><small className="text-xs text-mute">Ban quản trị</small></span></div><button onClick={() => navigate('/login')} className="flex h-10 w-full items-center gap-3 rounded-ui px-3 text-sm font-semibold text-mute hover:bg-soft"><Icon name="logout" className="text-lg" />Đăng xuất</button></div>
    </aside>
    <div className="lg:pl-64"><header className="sticky top-0 z-20 flex h-[72px] items-center justify-between border-b border-line bg-surface/95 px-4 backdrop-blur sm:px-6"><div className="flex items-center gap-3"><button className="text-xl lg:hidden" onClick={() => setOpen(true)} aria-label="Mở menu"><Icon name="menu" /></button><div className="text-sm"><span className="text-mute">Trang chủ</span><span className="mx-2 text-line">/</span><b>{labels[segment]}</b></div></div><div className="flex items-center gap-3"><div className="hidden w-64 sm:block"><div className="flex h-10 items-center gap-2 rounded-ui border border-line px-3 text-sm text-mute"><Icon name="search" />Tìm nhanh...</div></div><button className="relative flex size-10 items-center justify-center rounded-ui border border-line text-mute"><Icon name="bell" /><span className="absolute top-2 right-2 size-1.5 rounded-full bg-accent" /></button><div className="relative"><button onClick={() => setProfile(!profile)} className="flex size-10 items-center justify-center rounded-full bg-soft text-xs font-bold text-soft-ink">VA</button>{profile && <div className="absolute top-12 right-0 w-52 rounded-ui border border-line bg-surface p-2 shadow-lg"><p className="px-2 py-1 text-sm font-semibold">Nguyễn Văn An</p><button onClick={() => navigate('/login')} className="w-full rounded-ui px-2 py-2 text-left text-sm hover:bg-soft">Đăng xuất</button></div>}</div></div></header><main className="p-4 sm:p-6"><Outlet /></main></div>
  </div>
}
