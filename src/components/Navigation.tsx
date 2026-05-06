export default function Navigation({ currentScreen, setCurrentScreen }: { currentScreen: string, setCurrentScreen: (screen: string) => void }) {
  const tabs = [
    { id: 'CORE', icon: 'rocket_launch', label: 'CORE' },
    { id: 'LIBRARY', icon: 'graphic_eq', label: 'LIBRARY' },
    { id: 'SOUND_FORGE', icon: 'precision_manufacturing', label: 'FORGE' },
    { id: 'TRAPS', icon: 'alarm_on', label: 'TRAPS' },
    { id: 'SYSTEM', icon: 'settings_input_component', label: 'SYSTEM' },
  ];

  return (
    <nav className="fixed bottom-0 w-full z-50 flex justify-around items-center px-4 pb-6 pt-2 bg-zinc-900/90 backdrop-blur-2xl shadow-[0_-8px_30px_rgba(0,0,0,0.8)] border-t border-white/10 rounded-t-2xl divide-x divide-white/5">
      {tabs.map((tab) => {
        const isActive = currentScreen === tab.id;
        return (
          <button
            key={tab.id}
            onClick={() => setCurrentScreen(tab.id)}
            className={`flex flex-col items-center justify-center transition-all duration-500 ease-out flex-1 py-2 ${
              isActive
                ? 'text-lime-400 drop-shadow-[0_0_12px_rgba(163,230,53,0.7)]'
                : 'text-zinc-500 opacity-60 hover:bg-white/5 hover:text-orange-400'
            }`}
          >
            <span className="material-symbols-outlined text-lg">{tab.icon}</span>
            <span className="font-['Space_Grotesk'] font-bold text-[10px] tracking-tight">{tab.label}</span>
          </button>
        );
      })}
    </nav>
  );
}
