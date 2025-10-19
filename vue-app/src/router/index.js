import { createRouter,createWebHistory} from "vue-router";
import Home from "@/views/Home/index.vue";
import Login from "@/views/Login/index.vue";
import Forget from "@/views/Login/forget.vue";
import Mall from "@/views/Mall/index.vue";

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
      component: () => import("@/views/Person/index.vue"),
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