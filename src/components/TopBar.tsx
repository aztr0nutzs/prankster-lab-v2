import { useState, useEffect } from 'react';

export default function TopBar({ screen, onSettingsClick }: { screen: string; onSettingsClick?: () => void }) {
  const [timeStr, setTimeStr] = useState('');
  
  useEffect(() => {
    const intv = setInterval(() => {
      const now = new Date();
      setTimeStr(`${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}.${Math.floor(now.getMilliseconds()/100)}`);
    }, 100);
    return () => clearInterval(intv);
  }, []);

  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-zinc-950/90 backdrop-blur-xl border-b border-lime-500/30 shadow-[0_4px_20px_rgba(163,230,53,0.15)] flex justify-between items-center w-full px-6 py-2 pb-3">
      <div className="flex flex-1 items-center gap-3">
      </div>
      <div className="flex-[3] flex flex-col items-center justify-center relative">
        <div className="absolute inset-0 bg-orange-500/10 blur-2xl rounded-full"></div>
        <img src="/prankster_logo.png" alt="Prankster Logo" className="h-16 md:h-20 object-contain drop-shadow-[0_0_15px_rgba(163,230,53,0.3)] relative z-10 hover:scale-105 transition-transform duration-500" />
        <span className="font-[system-ui] italic font-black uppercase text-sm md:text-base tracking-[0.4em] text-lime-400 drop-shadow-[0_0_8px_rgba(163,230,53,0.9)] mt-2 skew-x-[-15deg] leading-none pb-1">
            {screen === 'CORE' && `PRANK ENGINE // ${timeStr}`}
            {screen === 'LIBRARY' && 'AUDIO ARSENAL'}
            {screen === 'TRAPS' && 'TRAP DEPLOYMENT'}
            {screen === 'MESSAGES' && 'PRANK TEXTS'}
            {screen === 'SOUND_FORGE' && 'SOUND FORGE'}
        </span>
      </div>
      <div className="flex flex-1 items-center justify-end gap-3">
        {screen !== 'TRAPS' && (
          <button className="p-2 transition-all duration-300 active:scale-95 hover:bg-lime-500/20 text-lime-400 rounded-full hover:shadow-[0_0_15px_rgba(163,230,53,0.4)]">
            <span className="material-symbols-outlined">search</span>
          </button>
        )}
        <button onClick={onSettingsClick} className="p-2 transition-all duration-300 active:scale-95 hover:bg-orange-500/20 hover:text-orange-400 rounded-full text-orange-500 hover:shadow-[0_0_15px_rgba(249,115,22,0.4)] hover:-rotate-45">
          <span className="material-symbols-outlined">settings</span>
        </button>
      </div>
    </header>
  );
}
