(ns app.ui-header)

(defn back-btn []
  [:div.bg-gray-300.hover:bg-gray-400.text-gray-800.font-bold.py-2.px-4.rounded-full.w-12.h-12 "<"])

(defn ui-header []
  [:div.bg-white.p-4.drop-shadow-md
   [:div {:class "grid grid-cols-[auto,1fr] gap-4 items-center"}
    (back-btn)
    [:h1.text-gray-600.text-4xl.font-extrabold.leading-none.tracking-tight.text-gray-900.md:text-5xl.lg:text-6xl "Fraiser"]]])
