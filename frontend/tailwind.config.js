/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#10161d',
        panel: '#18222d',
        line: '#2c3b4d',
        mist: '#d7e0ea',
        amber: '#f0b429',
      },
    },
  },
  plugins: [],
}
