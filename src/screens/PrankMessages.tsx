import { useState } from 'react';

const CATEGORIES = ['FUNNY', 'CREEPY LITE', 'ROBOT', 'AWKWARD', 'PARTY', 'OFFICE', 'RANDOM'];

const TEMPLATES = [
  { id: 1, category: 'FUNNY', text: 'Important update: your couch has requested personal space.' },
  { id: 2, category: 'FUNNY', text: 'A rubber chicken has been assigned to your case.' },
  { id: 3, category: 'FUNNY', text: 'Your snack drawer has filed a complaint.' },
  { id: 4, category: 'FUNNY', text: 'This is a friendly reminder that your left sock knows what happened.' },
  { id: 5, category: 'CREEPY LITE', text: 'Not scary, but your hallway just made a noise in my imagination.' },
  { id: 6, category: 'CREEPY LITE', text: 'Your closet has requested better lighting.' },
  { id: 7, category: 'CREEPY LITE', text: 'The floorboards would like to speak with management.' },
  { id: 8, category: 'CREEPY LITE', text: 'A tiny ghost says your Wi-Fi password is emotionally confusing.' },
  { id: 9, category: 'ROBOT', text: 'BEEP. Your daily nonsense quota is now complete.' },
  { id: 10, category: 'ROBOT', text: 'Robot inspection result: suspiciously human.' },
  { id: 11, category: 'ROBOT', text: 'System scan complete. Snack levels critically low.' },
  { id: 12, category: 'ROBOT', text: 'Alert: friendship protocol activated.' },
  { id: 13, category: 'AWKWARD', text: 'This message was sent by mistake, but now we both have to live with it.' },
  { id: 14, category: 'AWKWARD', text: 'Quick question: why did my phone just autocorrect your name to “chaos”?' },
  { id: 15, category: 'AWKWARD', text: 'Your vibe has been reviewed. Results pending.' },
  { id: 16, category: 'OFFICE', text: 'Your imaginary meeting has been moved to never.' },
  { id: 17, category: 'OFFICE', text: 'The printer has entered goblin mode.' },
  { id: 18, category: 'OFFICE', text: 'Corporate has approved one tiny scream.' },
  { id: 19, category: 'PARTY', text: 'Emergency party status: snacks required. Not a real emergency.' },
  { id: 20, category: 'PARTY', text: 'Your dance permit has been conditionally approved.' },
  { id: 21, category: 'PARTY', text: 'Confetti has been emotionally deployed.' },
];

export default function PrankMessages() {
  const [selectedCategory, setSelectedCategory] = useState('FUNNY');
  const [messageText, setMessageText] = useState('Important update: your couch has requested personal space.');
  const [phoneNumber, setPhoneNumber] = useState('');

  const filteredTemplates = selectedCategory === 'RANDOM' ? TEMPLATES : TEMPLATES.filter(t => t.category === selectedCategory);

  const handleSendSMS = () => {
    // Web implementation of Android SMS Intent
    const url = `sms:${phoneNumber}?body=${encodeURIComponent(messageText)}`;
    window.location.href = url;
  };

  const handleShare = () => {
    if (navigator.share) {
      navigator.share({
        title: 'Prankster Lab',
        text: `${messageText}\n\n- Created with Prankster Lab`,
      }).catch(console.error);
    } else {
      alert('Sharing is not supported on this device/browser.');
    }
  };

  const generateRandom = () => {
    const randomTemplate = TEMPLATES[Math.floor(Math.random() * TEMPLATES.length)];
    setMessageText(randomTemplate.text);
  };

  return (
    <main className="pt-24 pb-32 px-margin space-y-lg">
      {/* Header Controls */}
      <section className="space-y-base relative">
        <div className="absolute -top-10 -right-10 w-40 h-40 bg-fuchsia-500/20 blur-3xl pointer-events-none rounded-full"></div>
        <h2 className="font-display-lg text-fuchsia-400 drop-shadow-[0_0_8px_rgba(217,70,239,0.4)] text-3xl font-bold leading-none sm:text-display-lg relative z-10">PRANK MESSAGES</h2>
        <p className="font-body-sm text-lime-400 opacity-90 mt-2 tracking-widest uppercase text-xs relative z-10">Harmless prank texts, sent transparently</p>
      </section>

      {/* Responsible Use Card */}
      <section>
        <div className="glass-panel p-4 rounded-xl border border-orange-500/50 flex flex-col gap-3 bg-orange-500/10 shadow-[0_4px_15px_rgba(0,0,0,0.3)] hover:shadow-[0_4px_25px_rgba(249,115,22,0.2)] transition-shadow">
          <div className="flex items-center gap-3 text-orange-400">
            <span className="material-symbols-outlined drop-shadow-[0_0_5px_rgba(249,115,22,0.5)]">info</span>
            <span className="font-label-caps tracking-widest text-xs font-bold">RESPONSIBLE USE</span>
          </div>
          <p className="text-body-sm text-orange-100/90 leading-relaxed">
            Messages must be sent from your real number or shared by you. Do not impersonate people, businesses, emergency services, banks, schools, employers, or official sources.
          </p>
        </div>
      </section>

      {/* Template Picker - Categories */}
      <section className="overflow-x-auto no-scrollbar -mx-margin px-margin">
        <div className="flex gap-gutter pb-2">
          {CATEGORIES.map(cat => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`flex-none px-6 py-2 rounded-full font-label-caps whitespace-nowrap transition-all border ${
                selectedCategory === cat 
                  ? 'bg-gradient-to-r from-lime-500 to-lime-400 text-black border-lime-400 shadow-[0_0_15px_rgba(163,230,53,0.4)] tracking-wide' 
                  : 'border-outline-variant text-on-surface hover:border-lime-400/50 hover:bg-white/5'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>
      </section>

      {/* Template List */}
      <section className="flex gap-4 overflow-x-auto no-scrollbar -mx-margin px-margin snap-x pb-4">
        {filteredTemplates.map(template => (
          <button 
            key={template.id}
            onClick={() => setMessageText(template.text)}
            className="flex-none w-64 glass-panel p-5 rounded-2xl text-left border border-white/5 hover:border-lime-400/50 transition-all snap-start shadow-[0_4px_15px_rgba(0,0,0,0.2)] hover:shadow-[0_8px_30px_rgba(163,230,53,0.15)] group"
          >
            <span className="material-symbols-outlined text-lime-400/30 group-hover:text-lime-400 mb-2 transition-colors">format_quote</span>
            <p className="text-body-sm text-on-surface line-clamp-3 leading-relaxed">{template.text}</p>
          </button>
        ))}
      </section>

      {/* Message Editor */}
      <section className="space-y-4">
        <div className="flex justify-between items-center bg-black/30 p-2 rounded-lg">
          <span className="font-label-caps text-fuchsia-400 tracking-widest pl-2">TEMPLATE / DRAFT</span>
          <button onClick={generateRandom} className="text-orange-400 bg-orange-500/10 px-3 py-1.5 rounded-full font-label-caps flex items-center gap-1 hover:bg-orange-500/20 hover:text-orange-300 transition-colors">
            <span className="material-symbols-outlined text-[16px]">shuffle</span> RANDOM
          </button>
        </div>
        <textarea
          value={messageText}
          onChange={(e) => setMessageText(e.target.value)}
          className="w-full h-32 bg-surface-container-lowest border-2 border-fuchsia-500/30 rounded-2xl p-5 text-on-surface focus:outline-none focus:border-fuchsia-400 focus:ring-4 focus:ring-fuchsia-400/10 transition-all font-body-sm resize-none shadow-[inset_0_2px_15px_rgba(0,0,0,0.5)]"
        ></textarea>
      </section>

      {/* Recipient */}
      <section className="space-y-4">
        <span className="font-label-caps text-cyan-400 tracking-widest ml-2">RECIPIENT (OPTIONAL)</span>
        <div className="relative group">
          <div className="absolute inset-y-0 left-4 flex items-center pointer-events-none">
            <span className="material-symbols-outlined text-cyan-400 group-focus-within:text-cyan-300 transition-colors">phone</span>
          </div>
          <input
            type="tel"
            value={phoneNumber}
            onChange={(e) => setPhoneNumber(e.target.value)}
            placeholder="Phone Number"
            className="w-full bg-surface-container-lowest border-2 border-cyan-500/30 rounded-2xl py-4 pl-12 pr-4 text-on-surface focus:outline-none focus:border-cyan-400 focus:ring-4 focus:ring-cyan-400/10 transition-all font-body-lg shadow-[inset_0_2px_15px_rgba(0,0,0,0.4)]"
          />
        </div>
      </section>

      {/* Actions */}
      <section className="grid grid-cols-1 gap-4 pt-8">
        <button 
          onClick={handleSendSMS}
          className="w-full py-5 bg-gradient-to-r from-lime-500 to-lime-400 text-black font-label-caps rounded-xl flex items-center justify-center gap-2 shadow-[0_0_25px_rgba(163,230,53,0.4)] hover:shadow-[0_0_35px_rgba(163,230,53,0.6)] active:scale-95 transition-all text-sm font-bold tracking-widest border border-lime-300"
        >
          <span className="material-symbols-outlined text-xl" style={{ fontVariationSettings: "'FILL' 1" }}>send</span>
          OPEN SMS APP
        </button>
        <button 
          onClick={handleShare}
          className="w-full py-4 bg-black/40 border border-orange-500/50 text-orange-400 font-label-caps rounded-xl flex items-center justify-center gap-2 hover:bg-orange-500/10 hover:border-orange-400 active:scale-95 transition-all text-sm tracking-widest border-dashed"
        >
          <span className="material-symbols-outlined text-xl">share</span>
          SHARE PRANK TEXT
        </button>
      </section>
    </main>
  );
}
