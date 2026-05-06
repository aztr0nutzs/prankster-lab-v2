import { useEffect } from 'react';

interface SettingsDrawerProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function SettingsDrawer({ isOpen, onClose }: SettingsDrawerProps) {
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
    return () => {
      document.body.style.overflow = 'auto';
    };
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-[100] flex justify-end">
      {/* Backdrop */}
      <div 
        className="absolute inset-0 bg-black/60 backdrop-blur-sm transition-opacity"
        onClick={onClose}
      />
      
      {/* Drawer */}
      <div className="relative w-80 max-w-[80vw] h-full bg-zinc-950 border-l border-lime-500/30 flex flex-col shadow-2xl animate-[slideInRight_0.3s_ease-out]">
        
        {/* Secondary Header Logo */}
        <div className="p-6 pt-10 border-b border-white/10 flex flex-col items-center justify-center bg-zinc-900/50 overflow-hidden">
           <img src="/prankster_header.png" alt="Prankster Header" className="w-[120%] max-w-none scale-[1.3] object-contain drop-shadow-[0_0_15px_rgba(249,115,22,0.4)]" />
           <p className="font-['Space_Grotesk'] text-[10px] uppercase tracking-widest text-lime-400 mt-4 font-bold relative z-10">System Configuration</p>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6 space-y-6">
          <section className="space-y-3">
             <h3 className="text-orange-500 font-label-caps tracking-widest text-xs border-b border-orange-500/20 pb-2">AUDIO ROUTING</h3>
             <button className="w-full flex items-center justify-between text-zinc-300 hover:text-white transition-colors bg-white/5 p-3 rounded-lg border border-white/5">
                <div className="flex items-center gap-3">
                  <span className="material-symbols-outlined text-lime-400">speaker</span>
                  <span className="font-body-sm">Output Device</span>
                </div>
                <span className="text-xs text-zinc-500 font-mono">Default</span>
             </button>
             <button className="w-full flex items-center justify-between text-zinc-300 hover:text-white transition-colors bg-white/5 p-3 rounded-lg border border-white/5">
                <div className="flex items-center gap-3">
                  <span className="material-symbols-outlined text-orange-500">volume_up</span>
                  <span className="font-body-sm">Master Volume</span>
                </div>
                <span className="text-xs text-lime-400 font-mono">100%</span>
             </button>
          </section>
          
          <section className="space-y-3">
             <h3 className="text-fuchsia-400 font-label-caps tracking-widest text-xs border-b border-fuchsia-500/20 pb-2">PREFERENCES</h3>
             <label className="w-full flex items-center justify-between text-zinc-300 hover:text-white transition-colors bg-white/5 p-3 rounded-lg border border-white/5 cursor-pointer">
                <div className="flex items-center gap-3">
                  <span className="material-symbols-outlined text-fuchsia-400">vibration</span>
                  <span className="font-body-sm">Haptic Feedback</span>
                </div>
                <input type="checkbox" className="accent-lime-400 w-4 h-4 cursor-pointer" defaultChecked />
             </label>
             <label className="w-full flex items-center justify-between text-zinc-300 hover:text-white transition-colors bg-white/5 p-3 rounded-lg border border-white/5 cursor-pointer">
                <div className="flex items-center gap-3">
                  <span className="material-symbols-outlined text-cyan-400">notifications_active</span>
                  <span className="font-body-sm">Allow Background Audio</span>
                </div>
                <input type="checkbox" className="accent-lime-400 w-4 h-4 cursor-pointer" />
             </label>
          </section>
        </div>
        
        {/* Footer */}
        <div className="p-6 border-t border-white/10 flex justify-between items-center bg-zinc-900/50">
          <span className="text-[10px] font-mono text-zinc-500">v1.2.0-chaos</span>
          <button onClick={onClose} className="px-4 py-2 border border-lime-500/50 text-lime-400 rounded-full font-label-caps text-[10px] hover:bg-lime-500/10 transition-colors">
            CLOSE
          </button>
        </div>
      </div>
    </div>
  );
}
