import { useState } from 'react';
import { forgeSound } from '../utils/audio';

export default function SoundForge() {
  const [params, setParams] = useState({
    pitch: 1.0,
    reverb: 0.5,
    delay: 0.2
  });
  const [isGenerating, setIsGenerating] = useState(false);

  const handleGenerate = () => {
    setIsGenerating(true);
    setTimeout(() => {
      forgeSound('SCI_FI_BLIP', { ...params, duration: 1500, intensity: 0.8 });
      setIsGenerating(false);
    }, 800);
  };

  return (
    <main className="min-h-screen flex items-center justify-center bg-black p-4 pt-16">
      
      {/* Exact Control Panel implementation using attached image */}
      <div className="relative w-full max-w-[1200px] flex justify-center items-center">
        <img 
          src="/control_panel.png" 
          alt="Control Panel" 
          className="w-full h-auto object-contain drop-shadow-[0_0_30px_rgba(0,0,0,0.8)]" 
        />

        {/* Overlay interactive zones */}
        
        {/* Randomize Button */}
        <button 
           className="absolute bottom-[4%] left-[21%] w-[6%] h-[12%] rounded border border-orange-500/30 bg-orange-500/10 hover:bg-orange-500/40 hover:shadow-[0_0_15px_rgba(255,165,0,0.8)] cursor-pointer focus:outline-none transition-all duration-300 flex items-center justify-center group"
           onClick={() => setParams({ pitch: Math.random()*2, reverb: Math.random(), delay: Math.random() })}
           title="Randomize"
           aria-label="Randomize Parameters"
        >
           <span className="material-symbols-outlined text-orange-400 opacity-0 group-hover:opacity-100 transition-opacity">casino</span>
        </button>

        {/* Sounds / Library Button */}
        <button 
           className="absolute bottom-[4%] left-[34%] w-[6%] h-[12%] rounded border border-lime-500/30 bg-lime-500/10 hover:bg-lime-500/40 hover:shadow-[0_0_15px_rgba(163,230,53,0.8)] cursor-pointer focus:outline-none transition-all duration-300 flex items-center justify-center group"
           title="Sounds Library"
           aria-label="Sounds Library"
        >
           <span className="material-symbols-outlined text-lime-400 opacity-0 group-hover:opacity-100 transition-opacity">library_music</span>
        </button>

        {/* Trigger Prank Button (Center) */}
        <button 
           onClick={handleGenerate}
           className="absolute bottom-[4%] left-[45%] w-[10%] h-[15%] rounded border border-fuchsia-500/30 bg-fuchsia-500/10 hover:bg-fuchsia-500/40 hover:shadow-[0_0_25px_rgba(217,70,239,0.8)] cursor-pointer focus:outline-none transition-all duration-300 flex items-center justify-center group"
           title="Trigger Prank"
           aria-label="Trigger Prank"
        >
           <span className="material-symbols-outlined text-fuchsia-400 text-3xl opacity-0 group-hover:opacity-100 transition-opacity animate-pulse">power_settings_new</span>
        </button>

        {/* Playback Button */}
        <button 
           className="absolute bottom-[4%] right-[32%] w-[6%] h-[12%] rounded border border-cyan-500/30 bg-cyan-500/10 hover:bg-cyan-500/40 hover:shadow-[0_0_15px_rgba(0,255,255,0.8)] cursor-pointer focus:outline-none transition-all duration-300 flex items-center justify-center group"
           title="Playback"
           aria-label="Playback"
        >
           <span className="material-symbols-outlined text-cyan-400 opacity-0 group-hover:opacity-100 transition-opacity">play_arrow</span>
        </button>

        {/* Prank Dwell Time */}
        <button 
           className="absolute bottom-[4%] right-[8%] w-[6%] h-[12%] rounded border border-yellow-500/30 bg-yellow-500/10 hover:bg-yellow-500/40 hover:shadow-[0_0_15px_rgba(255,255,0,0.8)] cursor-pointer focus:outline-none transition-all duration-300 flex items-center justify-center group"
           title="Prank Dwell Time"
           aria-label="Prank Dwell Time"
        >
           <span className="material-symbols-outlined text-yellow-400 opacity-0 group-hover:opacity-100 transition-opacity">timelapse</span>
        </button>

        {/* Left top volume knob region */}
        <button 
           className="absolute top-[8%] left-[7%] w-[5%] h-[10%] rounded-full border border-blue-500/30 bg-blue-500/10 hover:bg-blue-500/40 hover:shadow-[0_0_15px_rgba(59,130,246,0.8)] cursor-pointer focus:outline-none transition-all duration-300"
           title="Prank Volume"
        ></button>

        {/* Central Core area button (Prank Engine) */}
        <button 
           className="absolute top-[20%] left-[12%] w-[18%] h-[50%] rounded-full border-2 border-red-500/20 bg-red-500/5 hover:bg-red-500/20 hover:border-red-500/50 hover:shadow-[0_0_30px_rgba(239,68,68,0.5)] cursor-pointer focus:outline-none transition-all duration-300 flex items-center justify-center group"
           title="Prank Engine Core"
        >
           <div className="w-1/2 h-1/2 rounded-full border border-red-500/50 group-hover:animate-ping opacity-0 group-hover:opacity-100"></div>
        </button>

      </div>
    </main>
  );
}
