import { useState } from 'react';

export default function System() {
  const [haptics, setHaptics] = useState(true);
  const [backgroundAudio, setBackgroundAudio] = useState(false);
  const [autoKillswitch, setAutoKillswitch] = useState(true);
  const [volume, setVolume] = useState(80);

  return (
    <main className="pt-24 pb-32 px-4 lg:px-6 h-screen flex flex-col items-center">
      <div className="w-full max-w-4xl flex flex-col gap-6">
        
        {/* Header */}
        <section className="mb-2 shrink-0 border-b border-lime-500/30 pb-4 flex justify-between items-end">
          <div>
            <h2 className="font-['Space_Grotesk'] text-2xl text-lime-400 uppercase tracking-widest flex items-center gap-2 drop-shadow-[0_0_10px_rgba(163,230,53,0.3)] font-bold">
              <span className="material-symbols-outlined text-orange-500 text-3xl">settings_system_daydream</span>
              System Configuration
            </h2>
            <p className="font-mono text-zinc-400 mt-1 text-sm uppercase tracking-widest">Global preferences and overrides</p>
          </div>
          <div className="text-right">
             <span className="text-[10px] font-mono text-cyan-400 bg-cyan-500/10 px-2 py-1 rounded border border-cyan-500/30">FIRMWARE v1.2.0-chaos</span>
          </div>
        </section>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 flex-1 overflow-y-auto no-scrollbar pb-10">
          
          {/* Audio Output Settings */}
          <section className="bg-zinc-900/50 border border-zinc-800 rounded-xl p-5 space-y-4 shadow-[0_0_20px_rgba(0,0,0,0.5)]">
             <h3 className="text-orange-500 font-mono tracking-widest text-xs border-b border-orange-500/20 pb-2 mb-4">AUDIO ROUTING</h3>
             
             <div className="space-y-4">
               <div>
                  <div className="flex justify-between items-center text-[10px] font-mono mb-2">
                    <span className="text-zinc-400">MASTER VOLUME</span>
                    <span className="text-orange-400">{volume}%</span>
                  </div>
                  <input 
                    type="range" 
                    min={0} max={100} value={volume}
                    onChange={(e) => setVolume(Number(e.target.value))}
                    className="w-full accent-orange-500 bg-zinc-800 h-1.5 rounded-full appearance-none outline-none cursor-pointer hover:accent-orange-400" 
                  />
               </div>

               <div className="pt-2">
                  <div className="flex justify-between items-center text-[10px] font-mono mb-2">
                    <span className="text-zinc-400">OUTPUT DEVICE</span>
                  </div>
                  <select className="w-full bg-zinc-950 border border-zinc-700 rounded-lg p-3 text-sm font-mono text-zinc-300 focus:outline-none focus:border-orange-500 transition-colors">
                    <option>System Default</option>
                    <option>External Bluetooth Speaker</option>
                    <option>Headphones</option>
                  </select>
               </div>
             </div>
          </section>

          {/* Preferences Settings */}
          <section className="bg-zinc-900/50 border border-zinc-800 rounded-xl p-5 space-y-4 shadow-[0_0_20px_rgba(0,0,0,0.5)]">
             <h3 className="text-fuchsia-400 font-mono tracking-widest text-xs border-b border-fuchsia-500/20 pb-2 mb-4">PREFERENCES</h3>
             
             <div className="space-y-3">
               <label className="w-full flex items-center justify-between text-zinc-300 hover:text-white transition-colors bg-black/40 p-3 rounded-lg border border-white/5 cursor-pointer">
                  <div className="flex items-center gap-3">
                    <span className="material-symbols-outlined text-fuchsia-400">vibration</span>
                    <span className="font-mono text-sm">Haptic Feedback</span>
                  </div>
                  <input type="checkbox" className="accent-fuchsia-500 w-4 h-4 cursor-pointer" checked={haptics} onChange={e => setHaptics(e.target.checked)} />
               </label>
               
               <label className="w-full flex items-center justify-between text-zinc-300 hover:text-white transition-colors bg-black/40 p-3 rounded-lg border border-white/5 cursor-pointer">
                  <div className="flex items-center gap-3">
                    <span className="material-symbols-outlined text-cyan-400">notifications_active</span>
                    <span className="font-mono text-sm">Allow Background Audio</span>
                  </div>
                  <input type="checkbox" className="accent-cyan-500 w-4 h-4 cursor-pointer" checked={backgroundAudio} onChange={e => setBackgroundAudio(e.target.checked)} />
               </label>
             </div>
          </section>

          {/* Danger Zone */}
          <section className="bg-zinc-900/50 border border-error/30 bg-error/5 rounded-xl p-5 col-span-1 md:col-span-2 shadow-[0_0_20px_rgba(200,0,0,0.1)]">
             <h3 className="text-error font-mono tracking-widest text-xs border-b border-error/20 pb-2 mb-4">DANGER ZONE</h3>
             
             <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
               <label className="w-full flex items-center justify-between text-zinc-300 hover:text-white transition-colors bg-black/40 p-3 rounded-lg border border-error/20 cursor-pointer">
                  <div className="flex items-center gap-3">
                    <span className="material-symbols-outlined text-error">timer</span>
                    <span className="font-mono text-sm">Auto-Killswitch (5 mins)</span>
                  </div>
                  <input type="checkbox" className="accent-error w-4 h-4 cursor-pointer" checked={autoKillswitch} onChange={e => setAutoKillswitch(e.target.checked)} />
               </label>
               
               <button className="w-full flex items-center justify-center gap-3 text-error hover:bg-error hover:text-black transition-colors bg-error/10 p-3 rounded-lg border border-error">
                  <span className="material-symbols-outlined">delete_forever</span>
                  <span className="font-mono text-sm font-bold uppercase tracking-widest">Clear All Data</span>
               </button>
             </div>
          </section>

        </div>
      </div>
    </main>
  );
}
