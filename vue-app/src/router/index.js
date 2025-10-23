import { createRouter,createWebHistory} from "vue-router";
import Home from "@/views/Home/index.vue";
import Login from "@/views/Login/index.vue";
import Forget from "@/views/Login/forget.vue";
import Mall from "@/views/Mall/index.vue";
import MainLayout from "../views/layout/MainLayout.vue";
import Profile from "../views/user/Profile.vue";
import Orders from "../views/user/Orders.vue";
import MyRoutes from "../views/user/MyRoutes.vue";
import Appointments from "../views/user/Appointments.vue";
import BrowsingHistory from "../views/user/BrowsingHistory.vue";
import Settings from "../views/user/Settings.vue";
import Medals from "../views/user/Medals.vue";
import AMapLoader from '@amap/amap-jsapi-loader';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      component: Home,
    },
    {
      path: "/home",
      name: "home",
      component: Home,
    },
    {
      path: "/login",
      name: "login",
      component: Login,
    },
    {
      path: "/forget",
      name: "forget",
      component: Forget,
    },
    {
      path: "/heritage-mall",
      name: "heritage-mall",
      component: Mall,
      // children: [
      //   {
      //     path: "food",
      //     component: () => import("@/views/Mall/components/Food.vue"),
      //   },
      // ],
    },
    {
      path: "/food",
      component: () => import("@/views/Mall/components/Food.vue"),
    },
    {
      path: "/craft",
      component: () => import("@/views/Mall/components/Craft.vue"),
    },
    {
      path: "/user-center",
      component: MainLayout,
      children:[
        {
          path:'profile',
          name:'profile',
          component:Profile,
        },
        {
          path:'orders',
          name:'orders',
          component:Orders,
        },
        {
          path:'routes-section',
          name:'routes-section',
          component:MyRoutes,
        },
        {
          path:'appointments',
          name:'appointments',
          component:Appointments,
        },
        {
          path:'browsing',
          name:'browsing',
          component:BrowsingHistory,
        },
        {
          path:'settings',
          name:'settings',
          component:Settings,
        },
        {
          path:'medals',
          name:'medals',
          component:Medals,
        }
      ]
    },
    {
      path: "/shop-car",
      component: () => import("@/views/Mall/components/ShopCar.vue"),
    },
    {
      path: "/checkout",
      component: () => import("@/views/Mall/components/CheckOut.vue"),
    },
    {
      path: "/3d-workshop",
      component: () => import("@/views/WorkShop/index.vue"),
    },
    {
      path: "/heritage-commit",
      component: () => import("@/views/Commit/index.vue"),
    },
    {
      path: "/heritage-map",
      component: () => import("@/views/Map/index.vue"),
    }
  ],
  // 路由滚动行为定制
  scrollBehavior () {
    return {
      top: 0
    }
  }
});
export default router;